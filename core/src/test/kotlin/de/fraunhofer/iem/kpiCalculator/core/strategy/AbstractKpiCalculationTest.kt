/*
 * Copyright (c) 2024 Fraunhofer IEM. All rights reserved.
 *
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 *
 * SPDX-License-Identifier: MIT
 * License-Filename: LICENSE
 */

package de.fraunhofer.iem.kpiCalculator.core.strategy

import de.fraunhofer.iem.kpiCalculator.model.kpi.hierarchy.KpiCalculationResult
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class AbstractKpiCalculationTest {

    @Test
    fun calculateKpiEmptyChildren() {
        fun callback(
            successScores: List<Pair<KpiCalculationResult.Success, Double>>,
            failed: List<Pair<KpiCalculationResult, Double>>,
            additionalWeight: Double,
            hasIncompleteResults: Boolean
        ): Unit {
            fail()
        }

        val testStrategy = TestStrategy(callback = ::callback)

        val emptyRelaxed = testStrategy.calculateKpi(
            childScores = emptyList(),
            strict = false
        )

        assert(emptyRelaxed is KpiCalculationResult.Empty)

        val emptyStrict = testStrategy.calculateKpi(
            childScores = emptyList(),
            strict = true
        )

        assert(emptyStrict is KpiCalculationResult.Empty)
    }

    @Test
    fun calculateKpiSuccess() {

        val childScores: List<Pair<KpiCalculationResult, Double>> = listOf(
            Pair(KpiCalculationResult.Success(score = 10), 0.4),
            Pair(KpiCalculationResult.Success(score = 10), 0.4),
            Pair(KpiCalculationResult.Success(score = 10), 0.4),
        )

        fun callback(
            successScores: List<Pair<KpiCalculationResult.Success, Double>>,
            failed: List<Pair<KpiCalculationResult, Double>>,
            additionalWeight: Double,
            hasIncompleteResults: Boolean
        ) {
            assertEquals(successScores.size, childScores.size)
            assert(failed.isEmpty())
            assertEquals(additionalWeight, 0.0)
            assert(!hasIncompleteResults)
        }

        val testStrategy = TestStrategy(callback = ::callback)


        testStrategy.calculateKpi(childScores = childScores, strict = false)
        testStrategy.calculateKpi(childScores = childScores, strict = true)
    }

    @Test
    fun calculateKpiIncomplete() {

        val childScores: List<Pair<KpiCalculationResult, Double>> = listOf(
            Pair(KpiCalculationResult.Incomplete(score = 10, reason = "", additionalWeights = 0.2), 0.4),
            Pair(KpiCalculationResult.Incomplete(score = 10, reason = "", additionalWeights = 0.2), 0.4),
            Pair(KpiCalculationResult.Incomplete(score = 10, reason = "", additionalWeights = 0.2), 0.2),
        )

        fun callbackRelaxed(
            successScores: List<Pair<KpiCalculationResult.Success, Double>>,
            failed: List<Pair<KpiCalculationResult, Double>>,
            additionalWeight: Double,
            hasIncompleteResults: Boolean
        ) {
            assertEquals(successScores.size, childScores.size)
            assert(failed.isEmpty())
            assertEquals(additionalWeight, 0.0)
            assert(hasIncompleteResults)
        }

        val testStrategy = TestStrategy(callback = ::callbackRelaxed)
        testStrategy.calculateKpi(childScores = childScores, strict = false)

        fun callbackStrict(
            successScores: List<Pair<KpiCalculationResult.Success, Double>>,
            failed: List<Pair<KpiCalculationResult, Double>>,
            additionalWeight: Double,
            hasIncompleteResults: Boolean
        ) {
            assert(successScores.isEmpty())
            assert(failed.isEmpty())
            assertEquals(additionalWeight, 0.0)
            assert(hasIncompleteResults)
        }

        val testStrategyStrict = TestStrategy(callback = ::callbackStrict)
        testStrategyStrict.calculateKpi(childScores = childScores, strict = true)
    }

    @Test
    fun calculateKpiError() {

        val childScores: List<Pair<KpiCalculationResult, Double>> = listOf(
            Pair(KpiCalculationResult.Empty(), 0.4),
            Pair(KpiCalculationResult.Empty(), 0.4),
            Pair(KpiCalculationResult.Error(reason = ""), 0.2),
        )

        fun callback(
            successScores: List<Pair<KpiCalculationResult.Success, Double>>,
            failed: List<Pair<KpiCalculationResult, Double>>,
            additionalWeight: Double,
            hasIncompleteResults: Boolean
        ) {
            assert(successScores.isEmpty())
            assertEquals(failed.size, childScores.size)
            assertEquals(additionalWeight, 1.0)
            assert(hasIncompleteResults)
        }

        val testStrategy = TestStrategy(callback = ::callback)
        testStrategy.calculateKpi(childScores = childScores, strict = false)
        testStrategy.calculateKpi(childScores = childScores, strict = true)

        val childScoresMixed: List<Pair<KpiCalculationResult, Double>> = listOf(
            Pair(KpiCalculationResult.Empty(), 0.4),
            Pair(KpiCalculationResult.Success(score = 9), 0.4),
            Pair(KpiCalculationResult.Error(reason = ""), 0.2),
        )

        fun callbackMixed(
            successScores: List<Pair<KpiCalculationResult.Success, Double>>,
            failed: List<Pair<KpiCalculationResult, Double>>,
            additionalWeight: Double,
            hasIncompleteResults: Boolean
        ) {
            assertEquals(1, successScores.size)
            assertEquals(2, failed.size)
            assertEquals((0.4 + 0.2) / 1, additionalWeight)
            assert(hasIncompleteResults)
        }

        val testStrategyMixed = TestStrategy(callback = ::callbackMixed)
        testStrategyMixed.calculateKpi(childScores = childScoresMixed, strict = false)
        testStrategyMixed.calculateKpi(childScores = childScoresMixed, strict = true)
    }

    @Test
    fun calculateKpiMixed() {

        val childScores: List<Pair<KpiCalculationResult, Double>> = listOf(
            Pair(KpiCalculationResult.Success(score = 4), 0.15),
            Pair(KpiCalculationResult.Success(score = 4), 0.15),
            Pair(KpiCalculationResult.Incomplete(score = 4, reason = "", additionalWeights = 0.1), 0.3),
            Pair(KpiCalculationResult.Empty(), 0.2),
            Pair(KpiCalculationResult.Error(reason = ""), 0.2),
        )

        fun callbackRelaxed(
            successScores: List<Pair<KpiCalculationResult.Success, Double>>,
            failed: List<Pair<KpiCalculationResult, Double>>,
            additionalWeight: Double,
            hasIncompleteResults: Boolean
        ) {
            assertEquals(3, successScores.size)
            assertEquals(2, failed.size)
            assertEquals((0.4 / 3), additionalWeight)
            assert(hasIncompleteResults)
        }

        val testStrategyStrict = TestStrategy(callback = ::callbackRelaxed)
        testStrategyStrict.calculateKpi(childScores = childScores, strict = false)

        fun callbackStrict(
            successScores: List<Pair<KpiCalculationResult.Success, Double>>,
            failed: List<Pair<KpiCalculationResult, Double>>,
            additionalWeight: Double,
            hasIncompleteResults: Boolean
        ) {
            assertEquals(2, successScores.size)
            assertEquals(3, failed.size)
            assertEquals((0.3 + 0.2 + 0.2) / 2, additionalWeight)
            assert(hasIncompleteResults)
        }

        val testStrategyMixed = TestStrategy(callback = ::callbackStrict)
        testStrategyMixed.calculateKpi(childScores = childScores, strict = true)
    }
}
