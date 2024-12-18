package com.google.cloud.teleport.spanner.ddl;

import com.google.auto.value.AutoValue;
import com.google.cloud.spanner.Dialect;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import javafx.util.Pair;
import javax.annotation.Nullable;

@AutoValue
public abstract class GraphElementTable implements Serializable {
    private static final long serialVersionUID = 1L;
    public enum Kind {
        UNSPECIFIED,
        NODE,
        EDGE
    }

    @Nullable
    public abstract String name();
    @Nullable
    public abstract String baseTableName();

    public abstract Kind kind();

    public abstract Dialect dialect();

    public abstract ImmutableList<String> keyColumns();

    public static class LabelToPropertyDefinitions {
        public LabelToPropertyDefinitions(String labelName,
            ImmutableList<Pair<String, String>> propertyDefinitions) {
            this.labelName = labelName;
            this.propertyDefinitions = propertyDefinitions;
        }
        public String labelName;
        // A propertyDefinition is a <propertyName> and its <valueExpressionString>
        public ImmutableList<Pair<String, String>> propertyDefinitions;
        public String prettyPrint() {
            StringBuilder sb = new StringBuilder();
            sb.append("LABEL ").append(labelName);
            StringJoiner propertyJoiner =
                new StringJoiner(", ", " PROPERTIES(", ")");
            for (Pair<String, String> propertyDefinition : propertyDefinitions) {
                String propertyName = propertyDefinition.getKey();
                String valueExpressionString = propertyDefinition.getValue();
                if (valueExpressionString.equals(propertyName)) {
                    propertyJoiner.add(propertyName);
                } else {
                    // sb.append(valueExpressionString).append(" AS ").append(propertyName);
                    StringBuilder aliasedProperty = new StringBuilder();
                    aliasedProperty.append(valueExpressionString)
                        .append(" AS ")
                        .append(propertyName);
                    propertyJoiner.add(aliasedProperty);
                }
            }
            sb.append(propertyDefinitions.isEmpty()
                ? " NO PROPERTIES"
                : propertyJoiner.toString());
            return sb.toString();
        }
    }
    public abstract ImmutableList<LabelToPropertyDefinitions> labelToPropertyDefinitions();

    public static class GraphNodeTableReference {
        public GraphNodeTableReference(String nodeTableName, ImmutableList<String> nodeKeyColumns,
            ImmutableList<String> edgeKeyColumns) {
            this.nodeTableName = nodeTableName;
            this.nodeKeyColumns = nodeKeyColumns;
            this.edgeKeyColumns = edgeKeyColumns;
        }
        public String nodeTableName;
        public ImmutableList<String> nodeKeyColumns;
        public ImmutableList<String> edgeKeyColumns;

        public String prettyPrint() {
            StringBuilder sb = new StringBuilder();
            sb.append("KEY(");
            sb.append(edgeKeyColumns.stream().collect(Collectors.joining(", ")));
            sb.append(") REFERENCES ").append(nodeTableName);
            return sb.toString();
        }
    }
    public abstract GraphNodeTableReference sourceNodeTable();
    public abstract GraphNodeTableReference targetNodeTable();

    public static GraphElementTable.Builder builder() {
        return builder(Dialect.GOOGLE_STANDARD_SQL);
    }

    public static GraphElementTable.Builder builder(Dialect dialect) {
        return new AutoValue_GraphElementTable.Builder()
            .dialect(dialect)
            .kind(Kind.UNSPECIFIED)
            .keyColumns(ImmutableList.of())
            .labelToPropertyDefinitions(ImmutableList.of())
            .sourceNodeTable(new GraphNodeTableReference("", ImmutableList.of(), ImmutableList.of()))
            .targetNodeTable(new GraphNodeTableReference("", ImmutableList.of(), ImmutableList.of()));
    }

    public abstract GraphElementTable.Builder autoToBuilder();

    public void prettyPrint(Appendable appendable) throws IOException {
        if (dialect() != Dialect.GOOGLE_STANDARD_SQL) {
            throw new IllegalArgumentException(String.format("Unrecognized Dialect: %s.",
                dialect()));
        }
        appendable.append(baseTableName());
        // Add alias if present
        if (!name().isEmpty()) {
            appendable.append(" AS ").append(name()).append("\n");
        }
        // Key columns
        String keyColumnsString = keyColumns().stream().collect(Collectors.joining(", "));
        appendable.append(" KEY (").append(keyColumnsString).append(")\n");

        // Source and target references for EDGE kind
        if (kind() == Kind.EDGE) {
            appendable.append("SOURCE ")
                .append(sourceNodeTable().prettyPrint())
                .append(" DESTINATION ")
                .append(targetNodeTable().prettyPrint())
                .append("\n");
        }

        // Labels and associated properties
        appendable.append(String.join("\n", labelToPropertyDefinitions().stream()
            .map(LabelToPropertyDefinitions::prettyPrint)
            .collect(Collectors.toList())));
    }
    public String prettyPrint() {
        StringBuilder sb = new StringBuilder();
        try {
            prettyPrint(sb);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return prettyPrint();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        private PropertyGraph.Builder propertyGraphBuilder;

        private LinkedHashMap<String, LabelToPropertyDefinitions> labelToPropertyDefinitions =
            Maps.newLinkedHashMap();

        Builder propertyGraphBuilder(PropertyGraph.Builder propertyGraphBuilder) {
            this.propertyGraphBuilder = propertyGraphBuilder;
            return this;
        }

        abstract GraphElementTable.Builder name(String name);
        abstract GraphElementTable.Builder baseTableName(String baseTableName);

        abstract GraphElementTable.Builder kind(Kind kind);



        public abstract GraphElementTable.Builder dialect(Dialect dialect);

        public abstract Builder keyColumns(ImmutableList<String> keyColumns);
        abstract Builder labelToPropertyDefinitions(
            ImmutableList<LabelToPropertyDefinitions> labelToPropertyDefinitions);
        abstract Builder sourceNodeTable(GraphNodeTableReference sourceNodeTable);
        abstract Builder targetNodeTable(GraphNodeTableReference targetNodeTable);

        abstract GraphElementTable autoBuild();

        public PropertyGraph.Builder endAddNodeTable() {
            propertyGraphBuilder.addNodeTable(this.autoBuild());
            return propertyGraphBuilder;
        }
        public PropertyGraph.Builder endAddEdgeTable() {
            propertyGraphBuilder.addEdgeTable(this.autoBuild());
            return propertyGraphBuilder;
        }
    }
}
