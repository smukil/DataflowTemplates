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

public abstract class GraphElementTable implements Serializable {
    private static final long serialVersionUID = 1L;

    @Nullable
    public abstract String name();

    public static GraphElementTable.Builder builder() {
        return builder(Dialect.GOOGLE_STANDARD_SQL);
    }

    public static GraphElementTable.Builder builder(Dialect dialect) {
        return new AutoValue_GraphElementTable.Builder().dialect(dialect).options(ImmutableList.of());
    }

    public abstract GraphElementTable.Builder autoToBuilder();

    @Override
    public String toString() {
        return prettyPrint();
    }

}
