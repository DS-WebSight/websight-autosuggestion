package pl.ds.websight.autosuggestion.util;

import org.apache.jackrabbit.api.security.user.QueryBuilder;

public final class QueryUtil {

    private QueryUtil() {
        //no instances
    }

    public static <Q> Q caseInsensitiveLike(QueryBuilder<Q> builder, String property, String value) {
        return builder.like("fn:lower-case(" + property + ")", "%" + value.toLowerCase() + "%");
    }
}
