package com.google.cloud.teleport.spanner.ddl;

import com.google.auto.value.AutoValue;
import com.google.cloud.spanner.Dialect;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;
import javax.annotation.Nullable;


@AutoValue
public abstract class PropertyGraph implements Serializable {
    private static final long serialVersionUID = 1L;

    @Nullable
    public abstract String name();

    public static class GraphElementLabel {
        public GraphElementLabel(String name, ImmutableList<String> properties) {
            this.name = name;
            this.properties = properties;
        }
        public String name;
        // public Set<String> properties;
        public ImmutableList<String> properties;
    }

    public static class PropertyDeclaration {
        public PropertyDeclaration(String name, String type) {
            this.name = name;
            this.type = type;
        }
        public String name;
        public String type;
    }
    public abstract ImmutableList<GraphElementTable> nodeTables();
    public abstract ImmutableList<GraphElementTable> edgeTables();
    public abstract ImmutableList<PropertyDeclaration> propertyDeclarations();
    public abstract ImmutableList<GraphElementLabel> labels();

    public abstract Dialect dialect();

    public static PropertyGraph.Builder builder() {
        return builder(Dialect.GOOGLE_STANDARD_SQL);
    }

    public static PropertyGraph.Builder builder(Dialect dialect) {
        return new AutoValue_PropertyGraph.Builder().dialect(dialect);
    }

    public abstract PropertyGraph.Builder autoToBuilder();

    @Override
    public String toString() {
        return prettyPrint();
    }

    public void prettyPrint(Appendable appendable) throws IOException {
        appendable.append("CREATE PROPERTY GRAPH ").append(name());
        appendable.append("\nNODE TABLES(\n");
        appendable.append(String.join(", ", nodeTables().stream()
            .map(GraphElementTable::prettyPrint)
            .collect(Collectors.toList())));
        appendable.append(")"); // End NODE TABLES()

        appendable.append("\nEDGE TABLES(\n");
        appendable.append(String.join(", ", edgeTables().stream()
            .map(GraphElementTable::prettyPrint)
            .collect(Collectors.toList())));
        appendable.append(");"); // End EDGE TABLES()
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

    @AutoValue.Builder
    public abstract static class Builder {
        private Ddl.Builder ddlBuilder;

        private LinkedHashMap<String, GraphElementTable> nodeTables = Maps.newLinkedHashMap();
        private LinkedHashMap<String, GraphElementTable> edgeTables = Maps.newLinkedHashMap();

        private LinkedHashMap<String, PropertyDeclaration> propertyDeclarations = Maps.newLinkedHashMap();
        private LinkedHashMap<String, GraphElementLabel> labels = Maps.newLinkedHashMap();

        public PropertyGraph.Builder ddlBuilder(Ddl.Builder ddlBuilder) {
            this.ddlBuilder = ddlBuilder;
            return this;
        }

        abstract PropertyGraph.Builder nodeTables(ImmutableList<GraphElementTable> nodeTables);

        abstract PropertyGraph.Builder edgeTables(ImmutableList<GraphElementTable> edgeTables);

        abstract PropertyGraph.Builder propertyDeclarations(ImmutableList<PropertyDeclaration> propertyDeclarations);

        abstract PropertyGraph.Builder labels(ImmutableList<GraphElementLabel> labels);

        public abstract PropertyGraph.Builder name(String name);

        public abstract String name();

        public abstract PropertyGraph.Builder dialect(Dialect dialect);

        public abstract Dialect dialect();

        abstract PropertyGraph autoBuild();

        public PropertyGraph build() {
            return nodeTables(ImmutableList.copyOf(nodeTables.values()))
                .edgeTables(ImmutableList.copyOf(edgeTables.values()))
                .propertyDeclarations(ImmutableList.copyOf(propertyDeclarations.values()))
                .labels(ImmutableList.copyOf(labels.values()))
                .autoBuild();
        }

        public PropertyGraph.Builder addNodeTable(GraphElementTable elementTable) {
            nodeTables.put(elementTable.name().toLowerCase(), elementTable);
            return this;
        }
        public PropertyGraph.Builder addEdgeTable(GraphElementTable elementTable) {
            edgeTables.put(elementTable.name().toLowerCase(), elementTable);
            return this;
        }
        public PropertyGraph.Builder addPropertyDeclaration(
            PropertyDeclaration propertyDeclaration) {
            propertyDeclarations.put(propertyDeclaration.name.toLowerCase(), propertyDeclaration);
            return this;
        }
        public PropertyGraph.Builder addLabel(GraphElementLabel label) {
            labels.put(label.name.toLowerCase(), label);
            return this;
        }

        public Ddl.Builder endPropertyGraph() {
            ddlBuilder.addPropertyGraph(build());
            return ddlBuilder;
        }
    }
}
