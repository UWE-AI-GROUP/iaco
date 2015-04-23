/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com;

import engine.Path;
import java.util.List;
import java.util.SortedMap;
import learning.IterationInformation;
import problem.CLSDatum;
import softwareDesign.CLSClass;
import softwareDesign.EleganceDesign;

/**
 *
 * @author kieran
 */

public class SessionBean {

      private Path path;
    private String designName;
    private SortedMap< String, List< CLSDatum>> useTable;
    private List< CLSClass> freezeList;
    private int iteration;
    private int interactionCounter;
    private IterationInformation information;
    private List< EleganceDesign> archive;
    
    
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public String getDesignName() {
        return designName;
    }

    public void setDesignName(String designName) {
        this.designName = designName;
    }

    public SortedMap<String, List<CLSDatum>> getUseTable() {
        return useTable;
    }

    public void setUseTable(SortedMap<String, List<CLSDatum>> useTable) {
        this.useTable = useTable;
    }

    public List<CLSClass> getFreezeList() {
        return freezeList;
    }

    public void setFreezeList(List<CLSClass> freezeList) {
        this.freezeList = freezeList;
    }

    public int getIteration() {
        return iteration;
    }

    public void setIteration(int iteration) {
        this.iteration = iteration;
    }

    public int getInteractionCounter() {
        return interactionCounter;
    }

    public void setInteractionCounter(int interactionCounter) {
        this.interactionCounter = interactionCounter;
    }

    public IterationInformation getInformation() {
        return information;
    }

    public void setInformation(IterationInformation information) {
        this.information = information;
    }

    public List<EleganceDesign> getArchive() {
        return archive;
    }

    public void setArchive(List<EleganceDesign> archive) {
        this.archive = archive;
    }
}
