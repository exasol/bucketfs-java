package com.exasol.bucketfs.jsonrpc;

import jakarta.json.JsonStructure;
import jakarta.json.bind.annotation.JsonbProperty;

public class JsonRpcResponse {
    @JsonbProperty("result_jobid")
    private String jobId;
    @JsonbProperty("result_code")
    private int code;
    @JsonbProperty("result_name")
    private String name;
    @JsonbProperty("result_desc")
    private String description;
    @JsonbProperty("result_revision")
    private int revision;
    @JsonbProperty("result_output")
    private JsonStructure output;

    public String getJobId() {
        return this.jobId;
    }

    public void setJobId(final String jobId) {
        this.jobId = jobId;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(final int code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public int getRevision() {
        return this.revision;
    }

    public void setRevision(final int revision) {
        this.revision = revision;
    }

    public JsonStructure getOutput() {
        return this.output;
    }

    public void setOutput(final JsonStructure output) {
        this.output = output;
    }

    @Override
    public String toString() {
        return "Response [jobId=" + this.jobId + ", code=" + this.code + ", name=" + this.name + ", description="
                + this.description
                + ", revision=" + this.revision + ", output=" + this.output + "]";
    }

    // {"result_jobid": "11.14", "result_code": 0, "result_name": "OK", "result_desc": "Success", "result_revision":
    // 19, "result_output": ["/exa/etc/bucketfs.cfg", "/exa/etc/bucketfs_db.cfg"]}

    // {"result_jobid": "11.15", "result_code": 1, "result_name": "Exception", "result_desc": "JobError:
    // ERROR::ConfD: Given bucket mynewbucket3 already exists in bucketfs bfsdefault.", "result_revision": 19}

}