/**
 *    Copyright 2006-2020 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.generator.api.dom.java;

import java.util.List;
import java.util.Map;

/**
 * @author xiangwb
 */
public interface BaseMapper<M> {
    int insert(M model);

    int insertBatch(List<M> models);

    int deleteById(String id);

    int deleteByIds(List<String> ids);

    int update(M model);

    int updateBatch(List<M> models);

    M findById(String id);

    List<M> findByIds(List<String> ids);

    List<M> findAll();
}

