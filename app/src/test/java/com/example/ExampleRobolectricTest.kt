package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.GameFormulas
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Dungeon 100", appName)
  }

  @Test
  fun `verify level and rank formulas`() {
    val exp1 = GameFormulas.expForNextLevel(1)
    val exp50 = GameFormulas.expForNextLevel(50)
    assertTrue(exp50 > exp1)

    val rankF = GameFormulas.getRankName(1)
    val rankSS = GameFormulas.getRankName(100)
    assertTrue(rankF.contains("Rank F"))
    assertTrue(rankSS.contains("Rank SS"))
  }

  @Test
  fun `verify monster floor scaling`() {
    val floor1 = GameFormulas.generateMonsterForFloor(1)
    val floor100Boss = GameFormulas.generateMonsterForFloor(100)

    assertTrue(floor100Boss.isBoss)
    assertTrue(floor100Boss.maxHp > floor1.maxHp)
    assertTrue(floor100Boss.atk > floor1.atk)
  }
}
