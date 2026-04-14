package com.lesadrax.registrationclient.from.model;

import java.util.List;

public class VisibilityCondition {
    private String dependentFieldKey;
    private List<String> values;
    private boolean inverse;

    public VisibilityCondition(String dependentFieldKey, List<String> values, boolean inverse) {
        this.dependentFieldKey = dependentFieldKey;
        this.values = values;
        this.inverse = inverse;
    }

    public boolean evaluate(String dependentFieldValue) {
        boolean contains = values.contains(dependentFieldValue);
        return inverse ? !contains : contains;
    }

    public String getDependentFieldKey() {
        return dependentFieldKey;
    }

    public void setDependentFieldKey(String dependentFieldKey) {
        this.dependentFieldKey = dependentFieldKey;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public boolean isInverse() {
        return inverse;
    }

    public void setInverse(boolean inverse) {
        this.inverse = inverse;
    }
}
