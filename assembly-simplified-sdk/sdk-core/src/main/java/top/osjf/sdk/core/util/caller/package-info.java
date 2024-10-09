/*
 * Copyright 2024-? the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * This package is a collection of toolkits for integrating calls to the SDK framework for RXJava3.
 * <p>
 * It introduces two types of calls: synchronous and asynchronous, with the specific manifestation
 * element being the SDK's response body.
 * <p>
 * It implements response error retry (error type can be specified), failure retry (response unsuccessful),
 * failure retry interval (millisecond value of failure retry interval call), response custom consumption
 * (successful and failed consumption), and automatic cleaning of subscription resources (RXJava3's subscription
 * relationship is automatically terminated and resources are released).
 */
package top.osjf.sdk.core.util.caller;