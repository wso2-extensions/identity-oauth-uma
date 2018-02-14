package org.wso2.carbon.identity.oauth.uma.resource.endpoint;

import org.wso2.carbon.identity.oauth.uma.resource.endpoint.dto.ResourceDetailsDTO;

import javax.ws.rs.core.Response;

public abstract class ResourceRegistrationApiService {
    public abstract Response deleteResource(String resourceId);
    public abstract Response getResource(String resourceId);
    public abstract Response getResourceIds(String resourceOwnerId);
    public abstract Response registerResource(ResourceDetailsDTO resource);
    public abstract Response updateResource(String resourceId,ResourceDetailsDTO updateresource);
}

