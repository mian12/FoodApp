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
}
