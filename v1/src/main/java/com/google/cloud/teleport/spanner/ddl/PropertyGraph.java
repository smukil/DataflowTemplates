package com.google.cloud.teleport.spanner.ddl;

import static com.google.cloud.teleport.spanner.common.NameUtils.quoteIdentifier;

import com.google.auto.value.AutoValue;
import com.google.cloud.spanner.Dialect;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;

public abstract class PropertyGraph implements Serializable {
    private static final long serialVersionUID = 1L;

    @Nullable
    public abstract String name();

    public class GraphElementLabel {
        public String label;
        public Set<String> properties;
    }
    Set<GraphElementLabel> labels;

    public class PropertyDeclaration {
        public String propertyName;
        public String type;
    }
    Set<PropertyDeclaration> propertyDeclarations;
    public abstract Dialect dialect();

    public static PropertyGraph.Builder builder() {
        return builder(Dialect.GOOGLE_STANDARD_SQL);
    }

    public static PropertyGraph.Builder builder(Dialect dialect) {
        return new AutoValue_PropertyGraph.Builder().dialect(dialect).options(ImmutableList.of());
    }

    public abstract PropertyGraph.Builder autoToBuilder();

    @Override
    public String toString() {
        return prettyPrint();
    }

    public abstract static class Builder {
        private Ddl.Builder ddlBuilder;

        public PropertyGraph.Builder ddlBuilder(Ddl.Builder ddlBuilder) {
            this.ddlBuilder = ddlBuilder;
            return this;
        }

        public abstract PropertyGraph.Builder name(String name);

        public abstract String name();

        public abstract PropertyGraph.Builder dialect(Dialect dialect);

        public abstract Dialect dialect();

        abstract PropertyGraph autoBuild();

        public PropertyGraph build() {
//            return inputColumns(ImmutableList.copyOf(inputColumns.values()))
//                    .outputColumns(ImmutableList.copyOf(outputColumns.values()))
//                    .autoBuild();
        }

        public Ddl.Builder endPropertyGraph() {
            ddlBuilder.addPropertyGraph(build());
            return ddlBuilder;
        }
    }
}
