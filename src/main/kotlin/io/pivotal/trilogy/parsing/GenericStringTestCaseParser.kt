package io.pivotal.trilogy.parsing

import io.pivotal.trilogy.i18n.MessageCreator
import io.pivotal.trilogy.parsing.exceptions.test.BaseParseException
import io.pivotal.trilogy.parsing.exceptions.testcase.InvalidFormat
import io.pivotal.trilogy.testcase.GenericTrilogyTest
import io.pivotal.trilogy.testcase.GenericTrilogyTestCase
import io.pivotal.trilogy.testcase.MalformedTrilogyTest
import io.pivotal.trilogy.testcase.TestCaseFixtures

class GenericStringTestCaseParser(testCaseBody: String) : BaseStringTestCaseParser(testCaseBody) {
    init {
        validate()
    }

    override fun getTestCase(): GenericTrilogyTestCase {
        return GenericTrilogyTestCase(parseDescription(), parseTests(), parseTestHooks(), malformedTests())
    }

    private fun malformedTests(): List<MalformedTrilogyTest> {
        return testStrings.map {
            try {
                GenericStringTestParser(it).getTest()
                null
            } catch (e: BaseParseException) {
                MalformedTrilogyTest(e.testName, e.localizedMessage)
            }
        }.filterNotNull()
    }

    private fun parseTestHooks(): TestCaseFixtures {
        return TestCaseFixtures(
                beforeEachTest = parseHookSection("BEFORE EACH TEST"),
                afterEachTest = parseHookSection("AFTER EACH TEST"),
                beforeAll = parseHookSection("BEFORE ALL"),
                afterAll = parseHookSection("AFTER ALL")
        )
    }

    override fun validate() {
        if (testCaseBody.hasInvalidHeader()) throw InvalidFormat(MessageCreator.getI18nMessage("testCaseParser.errors.malformedHeader"))
    }

    private fun String.hasValidHeader(): Boolean = this.contains(Regex("^# TEST CASE\\s*$", RegexOption.MULTILINE))
    private fun String.hasInvalidHeader() = !this.hasValidHeader()

    private fun parseTests(): List<GenericTrilogyTest> {
        return testStrings.map {
            try {
                GenericStringTestParser(it).getTest()
            } catch (e: BaseParseException) {
                null
            }
        }.filterNotNull()
    }
}