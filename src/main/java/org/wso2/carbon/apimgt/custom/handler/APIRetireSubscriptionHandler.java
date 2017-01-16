package org.wso2.carbon.apimgt.custom.handler;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.model.APIIdentifier;
import org.wso2.carbon.apimgt.custom.handler.constants.HandlerConstants;
import org.wso2.carbon.apimgt.impl.dao.ApiMgtDAO;
import org.wso2.carbon.apimgt.impl.utils.APIMgtDBUtil;
import org.wso2.carbon.apimgt.impl.utils.APIUtil;
import org.wso2.carbon.governance.api.generic.GenericArtifactManager;
import org.wso2.carbon.governance.api.generic.dataobjects.GenericArtifact;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.registry.core.jdbc.handlers.Handler;
import org.wso2.carbon.registry.core.jdbc.handlers.RequestContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.wso2.carbon.apimgt.impl.utils.APIUtil.handleException;

/**
 * Created by anuruddha on 1/16/17.
 */
public class APIRetireSubscriptionHandler extends Handler {
    private static final Log log = LogFactory.getLog(APIRetireSubscriptionHandler.class);

    public void put(RequestContext requestContext) throws RegistryException {
        String apiStatus = requestContext.getResource().getProperty(HandlerConstants.API_STATUS);

        // Check API status is Retired
        if (StringUtils.isNotEmpty(apiStatus) && apiStatus.equals(HandlerConstants.API_STATUS_RETIRED)) {
            log.info("Removing Subscribers");
            // Get registry resource UUID
            String apiArtifactId = requestContext.getRegistry().get(requestContext.getResourcePath().getPath()).getUUID();
            GenericArtifactManager artifactManager = null;
            try {
                artifactManager = APIUtil.getArtifactManager(requestContext.getSystemRegistry(),
                        "api");
                GenericArtifact artifact = artifactManager.getGenericArtifact(apiArtifactId);
                String provider = artifact.getAttribute(HandlerConstants.API_PROVIDER_STRING);
                String apiName = artifact.getAttribute(HandlerConstants.API_NAME_STRING);
                String version = artifact.getAttribute(HandlerConstants.API_VERSION_STRING);

                // Create API Identifier object
                APIIdentifier apiIdentifier = new APIIdentifier(provider, apiName, version);
                removeAllSubscriptions(apiIdentifier);
                log.info("Removed Subscribers");
            } catch (APIManagementException e) {
                log.error("Error occurred while delete subscriber");
                throw new RegistryException(e.getMessage());
            }
        }

    }

    /**
     * Remove all the subscriptions for given api
     *
     * @param apiIdentifier API identifier object
     * @throws APIManagementException
     */
    private void removeAllSubscriptions(APIIdentifier apiIdentifier) throws APIManagementException {
        Connection conn = null;
        PreparedStatement ps = null;
        int apiId;

        try {
            conn = APIMgtDBUtil.getConnection();
            conn.setAutoCommit(false);
            apiId = ApiMgtDAO.getAPIID(apiIdentifier, conn);

            String sqlQuery = HandlerConstants.REMOVE_ALL_SUBSCRIPTIONS_SQL;
            ps = conn.prepareStatement(sqlQuery);
            ps.setInt(1, apiId);
            ps.executeUpdate();
            conn.commit();

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e1) {
                    log.error("Failed to rollback remove all subscription ", e1);
                }
            }
            handleException("Failed to remove all subscriptions data ", e);
        } finally {
            APIMgtDBUtil.closeAllConnections(ps, conn, null);
        }
    }
}
