package ir.atriatech.util.others

import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class MyStringFormat {

	companion object {
		private val numberFormat = NumberFormat.getNumberInstance(Locale.US)
	}
	fun getFormatCreditCard(creditCard: String): String {
		if (creditCard.isEmpty())
			return ""

		val stringBuilder = StringBuilder()
		stringBuilder.append(creditCard)
		val positions: MutableList<Int> = ArrayList()
		for (i in 1 until creditCard.length) {
			if (i % 5 == 0 && i < creditCard.length)
				positions.add(i - 1)
		}

		if (positions.size > 0)
			positions.forEach {
				stringBuilder.insert(it, "-")
			}

		return stringBuilder.toString()
	}

	fun getFormatCreditCardR(creditCard: String): String {
		if (creditCard.isEmpty())
			return ""

		val stringBuilder = StringBuilder()
		stringBuilder.append(creditCard)
		val positions: MutableList<Int> = ArrayList()
		for (i in 1 until creditCard.length) {
			if (i % 5 == 0 && i < creditCard.length)
				positions.add(i - 1)
		}

		if (positions.size > 0)
			positions.forEach {
				stringBuilder.insert(it, "-")
			}

		val cardNumber = stringBuilder.split("-")
		val stringBuilderR = StringBuilder()

		for (i in cardNumber.size - 1 downTo 0) {
			cardNumber[i].forEach {
				stringBuilderR.append(it)
			}

			if (i > 0)
				stringBuilderR.append("-")
		}

		return stringBuilderR.toString()
	}



}
