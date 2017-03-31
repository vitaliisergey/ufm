package by.intexsoft.ufm.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect
public class OutputResult
{
    @JsonProperty("clientId")
    public Long clientId;

    @JsonProperty("spentTotal")
    public Long spentTotal;

    @JsonProperty("isBig")
    public boolean isBig;
}
