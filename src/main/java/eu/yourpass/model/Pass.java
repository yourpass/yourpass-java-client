
package eu.yourpass.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "url",
        "dynamicData",
        "voided",
        "expirationDate",
        "createdAt",
        "updatedAt",
        "deletedAt",
        "firstRegisterAt",
        "lastRegisterAt",
        "firstUnregisterAt",
        "lastUnregisterAt",
        "templateId",
        "projectId",
        "devices"
})

@JsonIgnoreProperties(ignoreUnknown = true)
public class Pass {

    @JsonProperty("id")
    private String id;
    @JsonProperty("url")
    private String url;
    @JsonProperty("dynamicData")
    private Map<String, Object> dynamicData;
    @JsonProperty("voided")
    private Boolean voided;
    @JsonProperty("templateId")
    private String templateId;
    @JsonProperty("projectId")
    private String projectId;
    @JsonProperty("devices")
    private Map<String, Object> devices;


    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }

    @JsonProperty("dynamicData")
    public Map<String, Object> getDynamicData() {
        return dynamicData;
    }

    @JsonProperty("dynamicData")
    public void setDynamicData(Map<String, Object> dynamicData) {
        this.dynamicData = dynamicData;
    }

    @JsonProperty("voided")
    public Boolean getVoided() {
        return voided;
    }

    @JsonProperty("voided")
    public void setVoided(Boolean voided) {
        this.voided = voided;
    }

    @JsonProperty("templateId")
    public String getTemplateId() {
        return templateId;
    }

    @JsonProperty("templateId")
    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    @JsonProperty("projectId")
    public String getProjectId() {
        return projectId;
    }

    @JsonProperty("projectId")
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    @JsonProperty("devices")
    public Map<String, Object> getDevices() {
        return devices;
    }

    @JsonProperty("devices")
    public void setDevices(Map<String, Object> devices) {
        this.devices = devices;
    }

    @Override
    public String toString() {
        return "Pass{" +
                "id='" + id + '\'' +
                ", url='" + url + '\'' +
                ", dynamicData=" + dynamicData +
                ", voided=" + voided +
                ", templateId='" + templateId + '\'' +
                ", projectId='" + projectId + '\'' +
                ", devices=" + devices +
                '}';
    }
}