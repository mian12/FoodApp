package com.solution.alnahar.eatit.fcmModel;

import java.util.List;

public class MyResponse {


    //
//    {
//        "multicast_id": 6067933183121518571,
//            "success": 1,
//            "failure": 0,
//            "canonical_ids": 0,
//            "results": [
//        {
//            "message_id": "0:1531248328603781%10d354e810d354e8"
//        }
//    ]
//    }

    public   String multicast_id;
    public   int success;
    public   int failure;
    public   int canonical_ids;
    public List<Results> results;


    public String getMulticast_id() {
        return multicast_id;
    }

    public void setMulticast_id(String multicast_id) {
        this.multicast_id = multicast_id;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public int getFailure() {
        return failure;
    }

    public void setFailure(int failure) {
        this.failure = failure;
    }

    public int getCanonical_ids() {
        return canonical_ids;
    }

    public void setCanonical_ids(int canonical_ids) {
        this.canonical_ids = canonical_ids;
    }

    public List<Results> getResults() {
        return results;
    }

    public void setResults(List<Results> results) {
        this.results = results;
    }


}
