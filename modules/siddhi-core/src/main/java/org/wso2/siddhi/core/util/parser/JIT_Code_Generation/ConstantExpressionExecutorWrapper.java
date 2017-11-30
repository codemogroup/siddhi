package org.wso2.siddhi.core.util.parser.JIT_Code_Generation;

import org.wso2.siddhi.core.event.ComplexEvent;
import org.wso2.siddhi.core.executor.ConstantExpressionExecutor;
import org.wso2.siddhi.core.executor.ExpressionExecutor;
import org.wso2.siddhi.query.api.definition.Attribute;

public class ConstantExpressionExecutorWrapper extends ConstantExpressionExecutor {

    private ExpressionExecutor jitCompiledExpressionExecutor;
    private ExpressionExecutor expressionExecutor;

    public ConstantExpressionExecutorWrapper(Object value, Attribute.Type type) {
        super(value, type);
    }

    public Object execute(ComplexEvent event) {
        if (this.jitCompiledExpressionExecutor == null) {
            this.jitCompiledExpressionExecutor = new ByteCodeGenerator().build(this.expressionExecutor);
        }
        return this.jitCompiledExpressionExecutor.execute(event);
    }

    public void setExpressionExecutor(ExpressionExecutor expressionExecutor) {
        this.expressionExecutor = expressionExecutor;
    }
}
