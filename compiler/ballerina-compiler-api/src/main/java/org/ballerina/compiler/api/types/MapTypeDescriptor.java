/*
 *  Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.ballerina.compiler.api.types;

import org.ballerina.compiler.api.semantic.BallerinaTypeDesc;
import org.ballerina.compiler.api.semantic.TypesFactory;
import org.ballerina.compiler.api.symbol.ModuleID;
import org.wso2.ballerinalang.compiler.semantics.model.types.BMapType;

import java.util.Optional;

/**
 * Represents an array type descriptor.
 *
 * @since 1.3.0
 */
public class MapTypeDescriptor extends BallerinaTypeDesc {

    private TypeDescriptor memberTypeDesc;

    public MapTypeDescriptor(ModuleID moduleID,
                             BMapType mapType) {
        super(TypeDescKind.MAP, moduleID, mapType);
    }

    public Optional<TypeDescriptor> getMemberTypeDescriptor() {
        if (this.memberTypeDesc == null) {
            this.memberTypeDesc = TypesFactory.getTypeDescriptor(((BMapType) this.getBType()).constraint);
        }
        return Optional.ofNullable(this.memberTypeDesc);
    }

    @Override
    public String getSignature() {
        Optional<TypeDescriptor> memberTypeDescriptor = this.getMemberTypeDescriptor();
        return memberTypeDescriptor.map(typeDescriptor -> "map<" + typeDescriptor.getSignature() + ">").orElse("map<>");
    }
}
