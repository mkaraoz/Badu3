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

import java.util.List;

/**
 * @author mk
 */
public class Metadata
{
    private String projectName;
    private String headerName;
    private String docTable_reportedBy;
    private String docTable_approvedBy;
    private String docTable_customerRepresentative;
    private String docTable_releaseNo;
    private String docTable_releaseDate;
    private String startDate;
    private String endDate;
    private String method;
    private String serviceType;
    private List<String> scopeList;
    private List<String> testTeam;

    public String getProjectName()
    {
        return projectName;
    }

    public void setProjectName(String projectName)
    {
        this.projectName = projectName;
    }

    public String getHeaderName()
    {
        return headerName;
    }

    public void setHeaderName(String headerName)
    {
        this.headerName = headerName;
    }

    public String getDocTable_reportedBy()
    {
        return docTable_reportedBy;
    }

    public void setDocTable_reportedBy(String docTable_reportedBy)
    {
        this.docTable_reportedBy = docTable_reportedBy;
    }

    public String getDocTable_approvedBy()
    {
        return docTable_approvedBy;
    }

    public void setDocTable_approvedBy(String docTable_approvedBy)
    {
        this.docTable_approvedBy = docTable_approvedBy;
    }

    public String getDocTable_customerRepresentative()
    {
        return docTable_customerRepresentative;
    }

    public void setDocTable_customerRepresentative(String docTable_customerRepresentative)
    {
        this.docTable_customerRepresentative = docTable_customerRepresentative;
    }

    public String getDocTable_releaseNo()
    {
        return docTable_releaseNo;
    }

    public void setDocTable_releaseNo(String docTable_releaseNo)
    {
        this.docTable_releaseNo = docTable_releaseNo;
    }

    public String getDocTable_releaseDate()
    {
        return docTable_releaseDate;
    }

    public void setDocTable_releaseDate(String docTable_releaseDate)
    {
        this.docTable_releaseDate = docTable_releaseDate;
    }

    public String getStartDate()
    {
        return startDate;
    }

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

    public String getEndDate()
    {
        return endDate;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public String getMethod()
    {
        return method;
    }

    public void setMethod(String method)
    {
        this.method = method;
    }

    public String getServiceType()
    {
        return serviceType;
    }

    public void setServiceType(String serviceType)
    {
        this.serviceType = serviceType;
    }

    public List<String> getScopeList()
    {
        return scopeList;
    }

    public void setScopeList(List<String> scopeList)
    {
        this.scopeList = scopeList;
    }

    public List<String> getTestTeam()
    {
        return testTeam;
    }

    public void setTestTeam(List<String> testTeam)
    {
        this.testTeam = testTeam;
    }
}
