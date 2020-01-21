/*
 * Copyright 2019 mk.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tt.badu3.core.data;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author mk
 */
public class JsonParser
{
    public static Metadata parseMetadata(JsonObject json)
    {
        Gson gson = new Gson();
        Metadata metaData = new Metadata();
        metaData.setProjectName(json.get("projectName").getAsString());
        metaData.setHeaderName(json.get("headerName").getAsString());
        metaData.setDocTable_approvedBy(json.get("approvedBy").getAsString());
        metaData.setDocTable_customerRepresentative(json.get("customerRep").getAsString());
        metaData.setDocTable_releaseDate(json.get("releaseDate").getAsString());
        metaData.setDocTable_releaseNo(json.get("releaseNo").getAsString());
        metaData.setDocTable_reportedBy(json.get("preparedBy").getAsString());
        metaData.setStartDate(json.get("startDate").getAsString());
        metaData.setEndDate(json.get("endDate").getAsString());
        metaData.setServiceType(json.get("service").getAsString());
        metaData.setMethod(json.get("method").getAsString());

        Type scopeListType = new TypeToken<ArrayList<String>>()
        {
        }.getType();
        metaData.setScopeList(gson.fromJson(json.getAsJsonArray("scope"), scopeListType));

        Type testTeamListType = new TypeToken<ArrayList<String>>()
        {
        }.getType();
        metaData.setTestTeam(gson.fromJson(json.getAsJsonArray("testTeam"), testTeamListType));

        return metaData;
    }

    public static List<Vulnerability> parseVulnerabilities(JsonObject json)
    {
        Gson gson = new Gson();
        Type vulnListType = new TypeToken<ArrayList<Vulnerability>>()
        {
        }.getType();
        List<Vulnerability> vulnerabilities = gson.fromJson(json.getAsJsonArray("vulnerabilities"), vulnListType);
        SeveritySorter.stupidSort(vulnerabilities);
        return vulnerabilities;
    }
}
