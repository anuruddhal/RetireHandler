# RetireHandler
Custom Registry Handler for API-M 1.8.0. Custom handler will remove all the subscriptions when API life-cycle changed to retired.

Steps
-----
1. Stop the server if running.
2. Copy the 'APIRetireSubscriptionHandler-1.0-SNAPSHOT.jar' into
	APIM_HOME/repository/components/dropins.

3. Edit the registry.xml file which is in 'APIM_HOME/repository/conf' folder with the following xml snippet.

    	 <handler class="org.wso2.carbon.apimgt.custom.handler.APIRetireSubscriptionHandler" methods="PUT">
              <filter class="org.wso2.carbon.registry.core.jdbc.handlers.filters.MediaTypeMatcher">
                <property name="mediaType">application/vnd.wso2-api+xml</property>
              </filter>
          </handler>

4. Start the server.