package org.wso2.siddhi.core.util.parser.JIT_Code_Generation;

import org.wso2.siddhi.core.event.ComplexEvent;
import org.wso2.siddhi.core.executor.ExpressionExecutor;
import org.wso2.siddhi.core.executor.condition.ConditionExpressionExecutor;

public class ConditionExpressionExecutorWrapper extends ConditionExpressionExecutor {

    private ExpressionExecutor jitCompiledExpressionExecutor;
    private ExpressionExecutor expressionExecutor;

    @Override
    public Boolean execute(ComplexEvent event) {
        if (this.jitCompiledExpressionExecutor == null) {
            this.jitCompiledExpressionExecutor = new ByteCodeGenerator().build(this.expressionExecutor);
        }
        return (Boolean) this.jitCompiledExpressionExecutor.execute(event);
    }

    @Override
    public ExpressionExecutor cloneExecutor(String key) {
        if (this.jitCompiledExpressionExecutor == null) {
            return this.expressionExecutor.cloneExecutor(key);
        }
        return this.jitCompiledExpressionExecutor.cloneExecutor(key);
    }

    public void setExpressionExecutor(ExpressionExecutor expressionExecutor) {
        this.expressionExecutor = expressionExecutor;
    }
}
