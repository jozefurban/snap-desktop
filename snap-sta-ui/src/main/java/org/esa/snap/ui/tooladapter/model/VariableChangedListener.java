package org.esa.snap.ui.tooladapter.model;

/**
 * Created by dmihailescu on 06/04/2016.
 */
public interface VariableChangedListener{
    void variableChanged(VariableChangedEvent ev);
}

class VariableChangedEvent{}
