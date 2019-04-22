/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bmuschko.gradle.clover

import org.gradle.api.InvalidUserDataException

import com.fasterxml.jackson.databind.ObjectMapper

import spock.lang.Specification

class CloverReportColumnSpec extends Specification {

    def "Construction works as expected"() {
        when: "Valid constructor arguments are used"
        def column = new CloverReportColumn('coveredBranches', [format: 'raw', min: '25', max: '75', scope: 'class'])

        then: "Name and attributes are correct"
        column.column == 'coveredBranches'
        column.attributes.size() == 4
        column.attributes.format == 'raw'
        column.attributes.min == '25'
        column.attributes.max == '75'
        column.attributes.scope == 'class'

        when: "Invalid column name is used"
        new CloverReportColumn('foobar', null)

        then: "Correct exception is thrown"
        def e = thrown(InvalidUserDataException)
        e.message == "Column 'foobar' is not supported"

        when: "Invalid format is used for raw columns"
        new CloverReportColumn('complexity', [format: '%'])

        then: "Correct exception is thrown"
        e = thrown(InvalidUserDataException)
        e.message == "Invalid column format specification '%' for column complexity"

        when: "Invalid format is used for multi format columns"
        new CloverReportColumn('coveredBranches', [format: 'foo'])

        then: "Correct exception is thrown"
        e = thrown(InvalidUserDataException)
        e.message == "Invalid column format specification 'foo' for column coveredBranches"

        when: "Invalid min is used"
        new CloverReportColumn('complexity', [format: 'raw', min: 'foo'])

        then: "Correct exception is thrown"
        e = thrown(InvalidUserDataException)
        e.message == "Invalid column min/max specification 'foo' for column complexity"

        when: "Invalid max is used"
        new CloverReportColumn('complexity', [format: 'raw', max: 'foo'])

        then: "Correct exception is thrown"
        e = thrown(InvalidUserDataException)
        e.message == "Invalid column min/max specification 'foo' for column complexity"

        when: "Invalid scope is used"
        new CloverReportColumn('complexity', [format: 'raw', scope: 'foo'])

        then: "Correct exception is thrown"
        e = thrown(InvalidUserDataException)
        e.message == "Invalid column scope specification 'foo' for column complexity"
    }
    
    def 'test conversions to and from JSON' () {
        given:
        CloverReportColumn column = new CloverReportColumn('coveredBranches', [format: 'raw', min: '25', max: '75', scope: 'class'])
        
        when:
        String jsonColumn = column.toJson()
        
        then:
        jsonColumn == '{"column":"coveredBranches","attributes":{"format":"raw","max":"75","min":"25","scope":"class"}}'
        
        when:
        CloverReportColumn columnFromJson = CloverReportColumn.fromJson(jsonColumn)
        
        then:
        columnFromJson.column == column.column
        columnFromJson.attributes == column.attributes
        
        when:
        HistoricalAdded added = new HistoricalAdded()
        added.interval = 'months'
        added.range = 3
        
        String jsonAdded = added.toJson()
        
        then:
        jsonAdded == '{"range":3,"interval":"months"}'
        
        and:
        HistoricalAdded addedFromJson = HistoricalAdded.fromJson(jsonAdded)
        addedFromJson.interval == added.interval
        addedFromJson.range == added.range
        
        when:
        HistoricalMover mover = new HistoricalMover()
        mover.interval = 'days'
        mover.range = 30
        mover.threshold = 5
        String jsonMover = mover.toJson()
        
        then:
        jsonMover == '{"threshold":5,"range":30,"interval":"days"}'
        
        and:
        HistoricalMover moverFromJson = HistoricalMover.fromJson(jsonMover)
        moverFromJson.interval == mover.interval
        moverFromJson.range == mover.range
        moverFromJson.threshold == mover.threshold
    }
}
