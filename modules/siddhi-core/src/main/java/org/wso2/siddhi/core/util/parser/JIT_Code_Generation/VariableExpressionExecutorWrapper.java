package org.wso2.siddhi.core.util.parser.JIT_Code_Generation;

import org.wso2.siddhi.core.event.ComplexEvent;
import org.wso2.siddhi.core.executor.ExpressionExecutor;
import org.wso2.siddhi.core.executor.VariableExpressionExecutor;
import org.wso2.siddhi.query.api.definition.Attribute;

import static org.wso2.siddhi.core.util.SiddhiConstants.STREAM_EVENT_CHAIN_INDEX;
import static org.wso2.siddhi.core.util.SiddhiConstants.STREAM_EVENT_INDEX_IN_CHAIN;
import static org.wso2.siddhi.core.util.SiddhiConstants.UNKNOWN_STATE;

public class VariableExpressionExecutorWrapper extends VariableExpressionExecutor {

    private ExpressionExecutor jitCompiledExpressionExecutor;
    private ExpressionExecutor expressionExecutor;

    public VariableExpressionExecutorWrapper(Attribute attribute, int streamEventChainIndex, int streamEventIndexInChain) {
        super(attribute, streamEventChainIndex, streamEventIndexInChain);
    }

    public Object execute(ComplexEvent event) {
        if (this.jitCompiledExpressionExecutor == null) {
            this.jitCompiledExpressionExecutor = new ByteCodeGenerator().build(this);
        }
        return this.jitCompiledExpressionExecutor.execute(event);
    }

    public void setExpressionExecutor(ExpressionExecutor expressionExecutor) {
        this.expressionExecutor = expressionExecutor;
    }
}
