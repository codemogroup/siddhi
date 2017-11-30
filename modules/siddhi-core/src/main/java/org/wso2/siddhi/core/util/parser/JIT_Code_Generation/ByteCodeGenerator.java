/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.siddhi.core.util.parser.JIT_Code_Generation;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.wso2.siddhi.core.exception.SiddhiAppRuntimeException;
import org.wso2.siddhi.core.executor.ConstantExpressionExecutor;
import org.wso2.siddhi.core.executor.ExpressionExecutor;
import org.wso2.siddhi.core.executor.VariableExpressionExecutor;
import org.wso2.siddhi.core.executor.condition.*;
import org.wso2.siddhi.core.executor.condition.compare.equal.*;
import org.wso2.siddhi.core.executor.condition.compare.greaterthan.*;
import org.wso2.siddhi.core.executor.condition.compare.greaterthanequal.*;
import org.wso2.siddhi.core.executor.condition.compare.lessthan.*;
import org.wso2.siddhi.core.executor.condition.compare.lessthanequal.*;
import org.wso2.siddhi.core.executor.condition.compare.notequal.*;
import org.wso2.siddhi.core.executor.math.add.AddExpressionExecutorDouble;
import org.wso2.siddhi.core.executor.math.add.AddExpressionExecutorFloat;
import org.wso2.siddhi.core.executor.math.add.AddExpressionExecutorInt;
import org.wso2.siddhi.core.executor.math.add.AddExpressionExecutorLong;
import org.wso2.siddhi.core.executor.math.divide.DivideExpressionExecutorDouble;
import org.wso2.siddhi.core.executor.math.divide.DivideExpressionExecutorFloat;
import org.wso2.siddhi.core.executor.math.divide.DivideExpressionExecutorInt;
import org.wso2.siddhi.core.executor.math.divide.DivideExpressionExecutorLong;
import org.wso2.siddhi.core.executor.math.mod.ModExpressionExecutorDouble;
import org.wso2.siddhi.core.executor.math.mod.ModExpressionExecutorFloat;
import org.wso2.siddhi.core.executor.math.mod.ModExpressionExecutorInt;
import org.wso2.siddhi.core.executor.math.mod.ModExpressionExecutorLong;
import org.wso2.siddhi.core.executor.math.multiply.MultiplyExpressionExecutorDouble;
import org.wso2.siddhi.core.executor.math.multiply.MultiplyExpressionExecutorFloat;
import org.wso2.siddhi.core.executor.math.multiply.MultiplyExpressionExecutorInt;
import org.wso2.siddhi.core.executor.math.multiply.MultiplyExpressionExecutorLong;
import org.wso2.siddhi.core.executor.math.subtract.SubtractExpressionExecutorDouble;
import org.wso2.siddhi.core.executor.math.subtract.SubtractExpressionExecutorFloat;
import org.wso2.siddhi.core.executor.math.subtract.SubtractExpressionExecutorInt;
import org.wso2.siddhi.core.executor.math.subtract.SubtractExpressionExecutorLong;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is the class that generates byte code for ExpressionExecutor.
 */
public class ByteCodeGenerator {

    static Map<Class<? extends ExpressionExecutor>, ByteCodeEmitter> byteCodeGenerators;

    static {
        byteCodeGenerators = new HashMap<Class<? extends ExpressionExecutor>, ByteCodeEmitter>();
        ByteCodeRegistry byteCode = new ByteCodeRegistry();
        // Condition Executors.
        byteCodeGenerators.put(AndConditionExpressionExecutor.class, byteCode.
                new PrivateANDExpressionExecutorBytecodeEmitter());
        byteCodeGenerators.put(OrConditionExpressionExecutor.class, byteCode.
                new PrivateORExpressionExecutorBytecodeEmitter());
        byteCodeGenerators.put(NotConditionExpressionExecutor.class, byteCode.
                new PrivateNOTExpressionExecutorBytecodeEmitter());
        byteCodeGenerators.put(VariableExpressionExecutorWrapper.class, byteCode.
                new PrivateVariableExpressionExecutorBytecodeEmitter());
        byteCodeGenerators.put(ConstantExpressionExecutor.class, byteCode.
                new PrivateConstantExpressionExecutorBytecodeEmitter());
        byteCodeGenerators.put(IsNullConditionExpressionExecutor.class,
                byteCode.new PrivateIsNullExpressionExecutorBytecodeEmitter());
        byteCodeGenerators.put(IsNullStreamConditionExpressionExecutor.class,
                byteCode.new PrivateIsNullStreamExpressionExecutorBytecodeEmitter());
        byteCodeGenerators.put(BoolConditionExpressionExecutor.class,
                byteCode.new PrivateBoolExpressionExecutorBytecodeEmitter());
        byteCodeGenerators.put(InConditionExpressionExecutor.class,
                byteCode.new PrivateInConditionExpressionExecutorBytecodeEmitter());
        // Greater Than Operator.
        byteCodeGenerators.put(GreaterThanCompareConditionExpressionExecutorFloatFloat.class,
                byteCode.new PrivateGreaterThanCompareConditionExpressionExecutorFloatFloatBytecodeEmitter());
        byteCodeGenerators.put(GreaterThanCompareConditionExpressionExecutorFloatDouble.class,
                byteCode.new PrivateGreaterThanCompareConditionExpressionExecutorFloatDoubleBytecodeEmitter());
        byteCodeGenerators.put(GreaterThanCompareConditionExpressionExecutorDoubleDouble.class,
                byteCode.new PrivateGreaterThanCompareConditionExpressionExecutorDoubleDoubleBytecodeEmitter());
        byteCodeGenerators.put(GreaterThanCompareConditionExpressionExecutorDoubleFloat.class,
                byteCode.new PrivateGreaterThanCompareConditionExpressionExecutorDoubleFloatBytecodeEmitter());
        byteCodeGenerators.put(GreaterThanCompareConditionExpressionExecutorDoubleInt.class,
                byteCode.new PrivateGreaterThanCompareConditionExpressionExecutorDoubleIntegerBytecodeEmitter());
        byteCodeGenerators.put(GreaterThanCompareConditionExpressionExecutorIntDouble.class,
                byteCode.new PrivateGreaterThanCompareConditionExpressionExecutorIntegerDoubleBytecodeEmitter());
        byteCodeGenerators.put(GreaterThanCompareConditionExpressionExecutorIntFloat.class,
                byteCode.new PrivateGreaterThanCompareConditionExpressionExecutorIntegerFloatBytecodeEmitter());
        byteCodeGenerators.put(GreaterThanCompareConditionExpressionExecutorFloatInt.class,
                byteCode.new PrivateGreaterThanCompareConditionExpressionExecutorFloatIntegerBytecodeEmitter());
        byteCodeGenerators.put(GreaterThanCompareConditionExpressionExecutorIntInt.class,
                byteCode.new PrivateGreaterThanCompareConditionExpressionExecutorIntegerIntegerBytecodeEmitter());
        byteCodeGenerators.put(GreaterThanCompareConditionExpressionExecutorDoubleLong.class,
                byteCode.new PrivateGreaterThanCompareConditionExpressionExecutorDoubleLongBytecodeEmitter());
        byteCodeGenerators.put(GreaterThanCompareConditionExpressionExecutorLongDouble.class,
                byteCode.new PrivateGreaterThanCompareConditionExpressionExecutorLongDoubleBytecodeEmitter());
        byteCodeGenerators.put(GreaterThanCompareConditionExpressionExecutorFloatLong.class,
                byteCode.new PrivateGreaterThanCompareConditionExpressionExecutorFloatLongBytecodeEmitter());
        byteCodeGenerators.put(GreaterThanCompareConditionExpressionExecutorLongFloat.class,
                byteCode.new PrivateGreaterThanCompareConditionExpressionExecutorLongFloatBytecodeEmitter());
        byteCodeGenerators.put(GreaterThanCompareConditionExpressionExecutorIntLong.class,
                byteCode.new PrivateGreaterThanCompareConditionExpressionExecutorIntegerLongBytecodeEmitter());
        byteCodeGenerators.put(GreaterThanCompareConditionExpressionExecutorLongInt.class,
                byteCode.new PrivateGreaterThanCompareConditionExpressionExecutorLongIntegerBytecodeEmitter());
        byteCodeGenerators.put(GreaterThanCompareConditionExpressionExecutorLongLong.class,
                byteCode.new PrivateGreaterThanCompareConditionExpressionExecutorLongLongBytecodeEmitter());
        // Less Than Operator.
        byteCodeGenerators.put(LessThanCompareConditionExpressionExecutorFloatFloat.class,
                byteCode.new PrivateLessThanCompareConditionExpressionExecutorFloatFloatBytecodeEmitter());
        byteCodeGenerators.put(LessThanCompareConditionExpressionExecutorFloatDouble.class,
                byteCode.new PrivateLessThanCompareConditionExpressionExecutorFloatDoubleBytecodeEmitter());
        byteCodeGenerators.put(LessThanCompareConditionExpressionExecutorDoubleDouble.class,
                byteCode.new PrivateLessThanCompareConditionExpressionExecutorDoubleDoubleBytecodeEmitter());
        byteCodeGenerators.put(LessThanCompareConditionExpressionExecutorDoubleFloat.class,
                byteCode.new PrivateLessThanCompareConditionExpressionExecutorDoubleFloatBytecodeEmitter());
        byteCodeGenerators.put(LessThanCompareConditionExpressionExecutorDoubleInt.class,
                byteCode.new PrivateLessThanCompareConditionExpressionExecutorDoubleIntegerBytecodeEmitter());
        byteCodeGenerators.put(LessThanCompareConditionExpressionExecutorIntDouble.class,
                byteCode.new PrivateLessThanCompareConditionExpressionExecutorIntegerDoubleBytecodeEmitter());
        byteCodeGenerators.put(LessThanCompareConditionExpressionExecutorIntFloat.class,
                byteCode.new PrivateLessThanCompareConditionExpressionExecutorIntegerFloatBytecodeEmitter());
        byteCodeGenerators.put(LessThanCompareConditionExpressionExecutorFloatInt.class,
                byteCode.new PrivateLessThanCompareConditionExpressionExecutorFloatIntegerBytecodeEmitter());
        byteCodeGenerators.put(LessThanCompareConditionExpressionExecutorLongLong.class,
                byteCode.new PrivateLessThanCompareConditionExpressionExecutorLongLongBytecodeEmitter());
        byteCodeGenerators.put(LessThanCompareConditionExpressionExecutorIntLong.class,
                byteCode.new PrivateLessThanCompareConditionExpressionExecutorIntegerLongBytecodeEmitter());
        byteCodeGenerators.put(LessThanCompareConditionExpressionExecutorLongInt.class,
                byteCode.new PrivateLessThanCompareConditionExpressionExecutorLongIntegerBytecodeEmitter());
        byteCodeGenerators.put(LessThanCompareConditionExpressionExecutorLongFloat.class,
                byteCode.new PrivateLessThanCompareConditionExpressionExecutorLongFloatBytecodeEmitter());
        byteCodeGenerators.put(LessThanCompareConditionExpressionExecutorFloatLong.class,
                byteCode.new PrivateLessThanCompareConditionExpressionExecutorFloatLongBytecodeEmitter());
        byteCodeGenerators.put(LessThanCompareConditionExpressionExecutorLongDouble.class,
                byteCode.new PrivateLessThanCompareConditionExpressionExecutorLongDoubleBytecodeEmitter());
        byteCodeGenerators.put(LessThanCompareConditionExpressionExecutorDoubleLong.class,
                byteCode.new PrivateLessThanCompareConditionExpressionExecutorDoubleLongBytecodeEmitter());
        byteCodeGenerators.put(LessThanCompareConditionExpressionExecutorIntInt.class,
                byteCode.new PrivateLessThanCompareConditionExpressionExecutorIntegerIntegerBytecodeEmitter());
        // Greater Than Equal Operator.
        byteCodeGenerators.put(GreaterThanEqualCompareConditionExpressionExecutorDoubleDouble.class,
                byteCode.new PrivateGreaterThanEqualCompareConditionExpressionExecutorDoubleDoubleBytecodeEmitter());
        byteCodeGenerators.put(GreaterThanEqualCompareConditionExpressionExecutorDoubleFloat.class,
                byteCode.new PrivateGreaterThanEqualCompareConditionExpressionExecutorDoubleFloatBytecodeEmitter());
        byteCodeGenerators.put(GreaterThanEqualCompareConditionExpressionExecutorDoubleInt.class,
                byteCode.new PrivateGreaterThanEqualCompareConditionExpressionExecutorDoubleIntegerBytecodeEmitter());
        byteCodeGenerators.put(GreaterThanEqualCompareConditionExpressionExecutorDoubleLong.class,
                byteCode.new PrivateGreaterThanEqualCompareConditionExpressionExecutorDoubleLongBytecodeEmitter());
        byteCodeGenerators.put(GreaterThanEqualCompareConditionExpressionExecutorFloatDouble.class,
                byteCode.new PrivateGreaterThanEqualCompareConditionExpressionExecutorFloatDoubleBytecodeEmitter());
        byteCodeGenerators.put(GreaterThanEqualCompareConditionExpressionExecutorFloatFloat.class,
                byteCode.new PrivateGreaterThanEqualCompareConditionExpressionExecutorFloatFloatBytecodeEmitter());
        byteCodeGenerators.put(GreaterThanEqualCompareConditionExpressionExecutorFloatInt.class,
                byteCode.new PrivateGreaterThanEqualCompareConditionExpressionExecutorFloatIntegerBytecodeEmitter());
        byteCodeGenerators.put(GreaterThanEqualCompareConditionExpressionExecutorFloatLong.class,
                byteCode.new PrivateGreaterThanEqualCompareConditionExpressionExecutorFloatLongBytecodeEmitter());
        byteCodeGenerators.put(GreaterThanEqualCompareConditionExpressionExecutorIntDouble.class,
                byteCode.new PrivateGreaterThanEqualCompareConditionExpressionExecutorIntegerDoubleBytecodeEmitter());
        byteCodeGenerators.put(GreaterThanEqualCompareConditionExpressionExecutorIntFloat.class,
                byteCode.new PrivateGreaterThanEqualCompareConditionExpressionExecutorIntegerFloatBytecodeEmitter());
        byteCodeGenerators.put(GreaterThanEqualCompareConditionExpressionExecutorIntInt.class,
                byteCode.new PrivateGreaterThanEqualCompareConditionExpressionExecutorIntegerIntegerBytecodeEmitter());
        byteCodeGenerators.put(GreaterThanEqualCompareConditionExpressionExecutorIntLong.class,
                byteCode.new PrivateGreaterThanEqualCompareConditionExpressionExecutorIntegerLongBytecodeEmitter());
        byteCodeGenerators.put(GreaterThanEqualCompareConditionExpressionExecutorLongDouble.class,
                byteCode.new PrivateGreaterThanEqualCompareConditionExpressionExecutorLongDoubleBytecodeEmitter());
        byteCodeGenerators.put(GreaterThanEqualCompareConditionExpressionExecutorLongFloat.class,
                byteCode.new PrivateGreaterThanEqualCompareConditionExpressionExecutorLongFloatBytecodeEmitter());
        byteCodeGenerators.put(GreaterThanEqualCompareConditionExpressionExecutorLongInt.class,
                byteCode.new PrivateGreaterThanEqualCompareConditionExpressionExecutorLongIntegerBytecodeEmitter());
        byteCodeGenerators.put(GreaterThanEqualCompareConditionExpressionExecutorLongLong.class,
                byteCode.new PrivateGreaterThanEqualCompareConditionExpressionExecutorLongLongBytecodeEmitter());
        //Less Than Equal Operator.
        byteCodeGenerators.put(LessThanEqualCompareConditionExpressionExecutorDoubleDouble.class,
                byteCode.new PrivateLessThanEqualCompareConditionExpressionExecutorDoubleDoubleBytecodeEmitter());
        byteCodeGenerators.put(LessThanEqualCompareConditionExpressionExecutorDoubleFloat.class,
                byteCode.new PrivateLessThanEqualCompareConditionExpressionExecutorDoubleFloatBytecodeEmitter());
        byteCodeGenerators.put(LessThanEqualCompareConditionExpressionExecutorDoubleInt.class,
                byteCode.new PrivateLessThanEqualCompareConditionExpressionExecutorDoubleIntegerBytecodeEmitter());
        byteCodeGenerators.put(LessThanEqualCompareConditionExpressionExecutorDoubleLong.class,
                byteCode.new PrivateLessThanEqualCompareConditionExpressionExecutorDoubleLongBytecodeEmitter());
        byteCodeGenerators.put(LessThanEqualCompareConditionExpressionExecutorFloatDouble.class,
                byteCode.new PrivateLessThanEqualCompareConditionExpressionExecutorFloatDoubleBytecodeEmitter());
        byteCodeGenerators.put(LessThanEqualCompareConditionExpressionExecutorFloatFloat.class,
                byteCode.new PrivateLessThanEqualCompareConditionExpressionExecutorFloatFloatBytecodeEmitter());
        byteCodeGenerators.put(LessThanEqualCompareConditionExpressionExecutorFloatInt.class,
                byteCode.new PrivateLessThanEqualCompareConditionExpressionExecutorFloatIntegerBytecodeEmitter());
        byteCodeGenerators.put(LessThanEqualCompareConditionExpressionExecutorFloatLong.class,
                byteCode.new PrivateLessThanEqualCompareConditionExpressionExecutorFloatLongBytecodeEmitter());
        byteCodeGenerators.put(LessThanEqualCompareConditionExpressionExecutorIntDouble.class,
                byteCode.new PrivateLessThanEqualCompareConditionExpressionExecutorIntegerDoubleBytecodeEmitter());
        byteCodeGenerators.put(LessThanEqualCompareConditionExpressionExecutorIntFloat.class,
                byteCode.new PrivateLessThanEqualCompareConditionExpressionExecutorIntegerFloatBytecodeEmitter());
        byteCodeGenerators.put(LessThanEqualCompareConditionExpressionExecutorIntInt.class,
                byteCode.new PrivateLessThanEqualCompareConditionExpressionExecutorIntegerIntegerBytecodeEmitter());
        byteCodeGenerators.put(LessThanEqualCompareConditionExpressionExecutorIntLong.class,
                byteCode.new PrivateLessThanEqualCompareConditionExpressionExecutorIntegerLongBytecodeEmitter());
        byteCodeGenerators.put(LessThanEqualCompareConditionExpressionExecutorLongDouble.class,
                byteCode.new PrivateLessThanEqualCompareConditionExpressionExecutorLongDoubleBytecodeEmitter());
        byteCodeGenerators.put(LessThanEqualCompareConditionExpressionExecutorLongFloat.class,
                byteCode.new PrivateLessThanEqualCompareConditionExpressionExecutorLongFloatBytecodeEmitter());
        byteCodeGenerators.put(LessThanEqualCompareConditionExpressionExecutorLongInt.class,
                byteCode.new PrivateLessThanEqualCompareConditionExpressionExecutorLongIntegerBytecodeEmitter());
        byteCodeGenerators.put(LessThanEqualCompareConditionExpressionExecutorLongLong.class,
                byteCode.new PrivateLessThanEqualCompareConditionExpressionExecutorLongLongBytecodeEmitter());
        // Equal Operator.
        byteCodeGenerators.put(EqualCompareConditionExpressionExecutorBoolBool.class,
                byteCode.new PrivateEqualCompareConditionExpressionExecutorBoolBoolBytecodeEmitter());
        byteCodeGenerators.put(EqualCompareConditionExpressionExecutorDoubleDouble.class,
                byteCode.new PrivateEqualCompareConditionExpressionExecutorDoubleDoubleBytecodeEmitter());
        byteCodeGenerators.put(EqualCompareConditionExpressionExecutorDoubleFloat.class,
                byteCode.new PrivateEqualCompareConditionExpressionExecutorDoubleFloatBytecodeEmitter());
        byteCodeGenerators.put(EqualCompareConditionExpressionExecutorDoubleInt.class,
                byteCode.new PrivateEqualCompareConditionExpressionExecutorDoubleIntegerBytecodeEmitter());
        byteCodeGenerators.put(EqualCompareConditionExpressionExecutorDoubleLong.class,
                byteCode.new PrivateEqualCompareConditionExpressionExecutorDoubleLongBytecodeEmitter());
        byteCodeGenerators.put(EqualCompareConditionExpressionExecutorFloatDouble.class,
                byteCode.new PrivateEqualCompareConditionExpressionExecutorFloatDoubleBytecodeEmitter());
        byteCodeGenerators.put(EqualCompareConditionExpressionExecutorFloatFloat.class,
                byteCode.new PrivateEqualCompareConditionExpressionExecutorFloatFloatBytecodeEmitter());
        byteCodeGenerators.put(EqualCompareConditionExpressionExecutorFloatInt.class,
                byteCode.new PrivateEqualCompareConditionExpressionExecutorFloatIntBytecodeEmitter());
        byteCodeGenerators.put(EqualCompareConditionExpressionExecutorFloatLong.class,
                byteCode.new PrivateEqualCompareConditionExpressionExecutorFloatLongBytecodeEmitter());
        byteCodeGenerators.put(EqualCompareConditionExpressionExecutorIntDouble.class,
                byteCode.new PrivateEqualCompareConditionExpressionExecutorIntegerDoubleBytecodeEmitter());
        byteCodeGenerators.put(EqualCompareConditionExpressionExecutorIntFloat.class,
                byteCode.new PrivateEqualCompareConditionExpressionExecutorIntegerFloatBytecodeEmitter());
        byteCodeGenerators.put(EqualCompareConditionExpressionExecutorIntInt.class,
                byteCode.new PrivateEqualCompareConditionExpressionExecutorIntegerIntegerBytecodeEmitter());
        byteCodeGenerators.put(EqualCompareConditionExpressionExecutorIntLong.class,
                byteCode.new PrivateEqualCompareConditionExpressionExecutorIntegerLongBytecodeEmitter());
        byteCodeGenerators.put(EqualCompareConditionExpressionExecutorLongDouble.class,
                byteCode.new PrivateEqualCompareConditionExpressionExecutorLongDoubleBytecodeEmitter());
        byteCodeGenerators.put(EqualCompareConditionExpressionExecutorLongFloat.class,
                byteCode.new PrivateEqualCompareConditionExpressionExecutorLongFloatBytecodeEmitter());
        byteCodeGenerators.put(EqualCompareConditionExpressionExecutorLongInt.class,
                byteCode.new PrivateEqualCompareConditionExpressionExecutorLongIntegerBytecodeEmitter());
        byteCodeGenerators.put(EqualCompareConditionExpressionExecutorLongLong.class,
                byteCode.new PrivateEqualCompareConditionExpressionExecutorLongLongBytecodeEmitter());
        byteCodeGenerators.put(EqualCompareConditionExpressionExecutorStringString.class,
                byteCode.new PrivateEqualCompareConditionExpressionExecutorStringStringBytecodeEmitter());
        // Not Equal Operator.
        byteCodeGenerators.put(NotEqualCompareConditionExpressionExecutorBoolBool.class,
                byteCode.new PrivateNotEqualCompareConditionExpressionExecutorBoolBoolBytecodeEmitter());
        byteCodeGenerators.put(NotEqualCompareConditionExpressionExecutorDoubleDouble.class,
                byteCode.new PrivateNotEqualCompareConditionExpressionExecutorDoubleDoubleBytecodeEmitter());
        byteCodeGenerators.put(NotEqualCompareConditionExpressionExecutorDoubleFloat.class,
                byteCode.new PrivateNotEqualCompareConditionExpressionExecutorDoubleFloatBytecodeEmitter());
        byteCodeGenerators.put(NotEqualCompareConditionExpressionExecutorDoubleInt.class,
                byteCode.new PrivateNotEqualCompareConditionExpressionExecutorDoubleIntegerBytecodeEmitter());
        byteCodeGenerators.put(NotEqualCompareConditionExpressionExecutorDoubleLong.class,
                byteCode.new PrivateNotEqualCompareConditionExpressionExecutorDoubleLongBytecodeEmitter());
        byteCodeGenerators.put(NotEqualCompareConditionExpressionExecutorFloatDouble.class,
                byteCode.new PrivateNotEqualCompareConditionExpressionExecutorFloatDoubleBytecodeEmitter());
        byteCodeGenerators.put(NotEqualCompareConditionExpressionExecutorFloatFloat.class,
                byteCode.new PrivateNotEqualCompareConditionExpressionExecutorFloatFloatBytecodeEmitter());
        byteCodeGenerators.put(NotEqualCompareConditionExpressionExecutorFloatInt.class,
                byteCode.new PrivateNotEqualCompareConditionExpressionExecutorFloatIntBytecodeEmitter());
        byteCodeGenerators.put(NotEqualCompareConditionExpressionExecutorFloatLong.class,
                byteCode.new PrivateNotEqualCompareConditionExpressionExecutorFloatLongBytecodeEmitter());
        byteCodeGenerators.put(NotEqualCompareConditionExpressionExecutorIntDouble.class,
                byteCode.new PrivateNotEqualCompareConditionExpressionExecutorIntegerDoubleBytecodeEmitter());
        byteCodeGenerators.put(NotEqualCompareConditionExpressionExecutorIntFloat.class,
                byteCode.new PrivateNotEqualCompareConditionExpressionExecutorIntegerFloatBytecodeEmitter());
        byteCodeGenerators.put(NotEqualCompareConditionExpressionExecutorIntInt.class,
                byteCode.new PrivateNotEqualCompareConditionExpressionExecutorIntegerIntegerBytecodeEmitter());
        byteCodeGenerators.put(NotEqualCompareConditionExpressionExecutorIntLong.class,
                byteCode.new PrivateNotEqualCompareConditionExpressionExecutorIntegerLongBytecodeEmitter());
        byteCodeGenerators.put(NotEqualCompareConditionExpressionExecutorLongDouble.class,
                byteCode.new PrivateNotEqualCompareConditionExpressionExecutorLongDoubleBytecodeEmitter());
        byteCodeGenerators.put(NotEqualCompareConditionExpressionExecutorLongFloat.class,
                byteCode.new PrivateNotEqualCompareConditionExpressionExecutorLongFloatBytecodeEmitter());
        byteCodeGenerators.put(NotEqualCompareConditionExpressionExecutorLongInt.class,
                byteCode.new PrivateNotEqualCompareConditionExpressionExecutorLongIntegerBytecodeEmitter());
        byteCodeGenerators.put(NotEqualCompareConditionExpressionExecutorLongLong.class,
                byteCode.new PrivateNotEqualCompareConditionExpressionExecutorLongLongBytecodeEmitter());
        byteCodeGenerators.put(NotEqualCompareConditionExpressionExecutorStringString.class,
                byteCode.new PrivateNotEqualCompareConditionExpressionExecutorStringStringBytecodeEmitter());
        //Mathematical Operators.
        byteCodeGenerators.put(SubtractExpressionExecutorDouble.class,
                byteCode.new PrivateSubtractExpressionExecutorDoubleBytecodeEmitter());
        byteCodeGenerators.put(SubtractExpressionExecutorFloat.class,
                byteCode.new PrivateSubtractExpressionExecutorFloatBytecodeEmitter());
        byteCodeGenerators.put(SubtractExpressionExecutorInt.class,
                byteCode.new PrivateSubtractExpressionExecutorIntBytecodeEmitter());
        byteCodeGenerators.put(SubtractExpressionExecutorLong.class,
                byteCode.new PrivateSubtractExpressionExecutorLongBytecodeEmitter());

        byteCodeGenerators.put(AddExpressionExecutorDouble.class,
                byteCode.new PrivateAddExpressionExecutorDoubleBytecodeEmitter());
        byteCodeGenerators.put(AddExpressionExecutorFloat.class,
                byteCode.new PrivateAddExpressionExecutorFloatBytecodeEmitter());
        byteCodeGenerators.put(AddExpressionExecutorInt.class,
                byteCode.new PrivateAddExpressionExecutorIntBytecodeEmitter());
        byteCodeGenerators.put(AddExpressionExecutorLong.class,
                byteCode.new PrivateAddExpressionExecutorLongBytecodeEmitter());

        byteCodeGenerators.put(MultiplyExpressionExecutorDouble.class,
                byteCode.new PrivateMultiplyExpressionExecutorDoubleBytecodeEmitter());
        byteCodeGenerators.put(MultiplyExpressionExecutorFloat.class,
                byteCode.new PrivateMultiplyExpressionExecutorFloatBytecodeEmitter());
        byteCodeGenerators.put(MultiplyExpressionExecutorInt.class,
                byteCode.new PrivateMultiplyExpressionExecutorIntBytecodeEmitter());
        byteCodeGenerators.put(MultiplyExpressionExecutorLong.class,
                byteCode.new PrivateMultiplyExpressionExecutorLongBytecodeEmitter());

        byteCodeGenerators.put(DivideExpressionExecutorDouble.class,
                byteCode.new PrivateDivideExpressionExecutorDoubleBytecodeEmitter());
        byteCodeGenerators.put(DivideExpressionExecutorFloat.class,
                byteCode.new PrivateDivideExpressionExecutorFloatBytecodeEmitter());
        byteCodeGenerators.put(DivideExpressionExecutorInt.class,
                byteCode.new PrivateDivideExpressionExecutorIntegerBytecodeEmitter());
        byteCodeGenerators.put(DivideExpressionExecutorLong.class,
                byteCode.new PrivateDivideExpressionExecutorLongBytecodeEmitter());

        byteCodeGenerators.put(ModExpressionExecutorDouble.class,
                byteCode.new PrivateModExpressionExecutorDoubleBytecodeEmitter());
        byteCodeGenerators.put(ModExpressionExecutorFloat.class,
                byteCode.new PrivateModExpressionExecutorFloatBytecodeEmitter());
        byteCodeGenerators.put(ModExpressionExecutorInt.class,
                byteCode.new PrivateModExpressionExecutorIntegerBytecodeEmitter());
        byteCodeGenerators.put(ModExpressionExecutorLong.class,
                byteCode.new PrivateModExpressionExecutorLongBytecodeEmitter());
        //Unhandled ExpressionExecutors.
        byteCodeGenerators.put(ExpressionExecutor.class,
                byteCode.new PrivateExtensionBytecodeEmitter());
    }

    private List<ExpressionExecutor> unHandledExpressionExecutors = new ArrayList<ExpressionExecutor>();
    private byte[] byteArray;
    private ExpressionExecutor expressionExecutor;

    /**
     * This method returns Expression executor with byte code.
     *
     * @param conditionExecutor
     * @return ExpressionExecutor which optimized using JIT code generation.
     */
    public ExpressionExecutor build(ExpressionExecutor conditionExecutor) {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        ByteCodeBuilder.jitCompiledExpressionExecutorClassPopulator(classWriter);
        ByteCodeBuilder.unHandledExpressionExecutorsFieldPopulator(classWriter);
        ByteCodeBuilder.cloneExecutorMethodPopulator(classWriter);
        ByteCodeBuilder.getReturnTypeMethodPopulator(classWriter);
        MethodVisitor executeMethodVisitor = ByteCodeBuilder.executeMethodPopulator(classWriter);
        this.execute(conditionExecutor, 2, 0, null, executeMethodVisitor, this);
        ByteCodeBuilder.end(classWriter, executeMethodVisitor, this, conditionExecutor);
        return this.expressionExecutor;
    }

    /**
     * This method maps ExpressionExecutor to it's ByteCodeEmitter.
     * @param conditionExecutor ExpressionExecutor that needs byte-code generation.
     * @param index             Local variable array index where data related to Expression Executor is stored.
     * @param parent            Used to indicate  ExpressionExecutor before Current ExpressionExecutor.
     *                          0 - all other ExpressionExecutors.
     *                          1 - and ExpressionExecutor.
     *                          2 - or ExpressionExecutor.
     *                          3 - not ExpressionExecutor.
     * @param specialCase       Used to optimize if two consecutive same kind of ExpressionExecutors are met.
     *                          Ex- In the case of 2 consecutive 'and' ExpressionExecutors, if first 'and' is false
     *                          then straight away will jump to instruction after second 'and' ExpressionExecutor.
     * @param methodVisitor     Instance of ASM library class 'MethodVisitor' that used to generate a method inside the
     *                          byte-code class.
     * @param byteCodeGenerator
     */
    protected void execute(ExpressionExecutor conditionExecutor, int index, int parent,
                           Label specialCase, MethodVisitor methodVisitor, ByteCodeGenerator byteCodeGenerator) {
        if (conditionExecutor != null) {
            if (ByteCodeGenerator.byteCodeGenerators.containsKey(conditionExecutor.getClass())) {
                ByteCodeGenerator.byteCodeGenerators.get(conditionExecutor.getClass()).generate(conditionExecutor,
                        index, parent, specialCase, methodVisitor, byteCodeGenerator);
            } else {
                ByteCodeGenerator.byteCodeGenerators.get(ExpressionExecutor.class).generate(conditionExecutor, index,
                        parent, specialCase, methodVisitor, byteCodeGenerator);
            }
        } else {
            throw new SiddhiAppRuntimeException("No ExpressionExecutor is given to generate JIT code.");
        }
    }

    public List<ExpressionExecutor> getUnHandledExpressionExecutors() {
        return unHandledExpressionExecutors;
    }

    public byte[] getByteArray() {
        return byteArray;
    }

    public void setByteArray(byte[] byteArray) {
        this.byteArray = byteArray;
    }

    public void setExpressionExecutor(ExpressionExecutor expressionExecutor) {
        this.expressionExecutor = expressionExecutor;
    }


}
