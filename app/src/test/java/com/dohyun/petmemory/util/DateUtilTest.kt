package com.dohyun.petmemory.util


import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

class DateUtilTest {

    @Test
    fun `테스트 입니다` () {

        val gap = DateUtil.getDateGap("2023-07-15", "2023-07-21")
        assertThat(gap).isEqualTo(8)

        runBlockingTest {

        }

    }

}