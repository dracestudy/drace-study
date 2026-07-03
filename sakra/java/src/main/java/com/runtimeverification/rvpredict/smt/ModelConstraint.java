package com.runtimeverification.rvpredict.smt;

import com.runtimeverification.rvpredict.smt.formula.FormulaTerm;

public interface ModelConstraint {
    FormulaTerm createSmtFormula();
    boolean evaluate(VariableSource variableSource);
}
