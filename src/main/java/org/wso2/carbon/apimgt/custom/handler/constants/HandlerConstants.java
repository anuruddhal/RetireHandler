package org.wso2.carbon.apimgt.custom.handler.constants;

/**
 * Created by anuruddha on 1/16/17.
 */
public class HandlerConstants {
    public static final String REMOVE_ALL_SUBSCRIPTIONS_SQL =
            " DELETE FROM AM_SUBSCRIPTION WHERE API_ID = ?";
    public static final String API_NAME_STRING =
            "overview_name";
    public static final String API_PROVIDER_STRING =
            "overview_provider";
    public static final String API_VERSION_STRING =
            "overview_version";
    public static final String API_STATUS_RETIRED =
            "RETIRED";
    public static final String API_STATUS =
            "STATUS";
}
