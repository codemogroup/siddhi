
package org.wso2.siddhi.core.util.parser.JIT_Code_Generation;

import org.wso2.siddhi.core.event.ComplexEvent;
import org.wso2.siddhi.core.executor.ExpressionExecutor;
import org.wso2.siddhi.query.api.definition.Attribute;

public class ExpressionExecutorWrapper implements ExpressionExecutor {

    private ExpressionExecutor jitCompiledExpressionExecutor;
    private ExpressionExecutor expressionExecutor;

    @Override
    public Object execute(ComplexEvent event) {
        if (this.jitCompiledExpressionExecutor == null) {
            this.jitCompiledExpressionExecutor = new ByteCodeGenerator().build(this.expressionExecutor);
        }
        return this.jitCompiledExpressionExecutor.execute(event);
    }

    @Override
    public Attribute.Type getReturnType() {
        if (this.jitCompiledExpressionExecutor == null) {
            return this.expressionExecutor.getReturnType();
        }
        return this.jitCompiledExpressionExecutor.getReturnType();
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
