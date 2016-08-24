package org.kt3k.gradle.plugin.coveralls.domain

import groovy.json.JsonSlurper
import org.gradle.api.Project

/**
 * Created by leboff on 8/18/16.
 */
class ForceCoverSourceReportFactory implements SourceReportFactory {
    @Override
    List<SourceReport> createReportList(Project project, File file){
        JsonSlurper slurper = new JsonSlurper()


        List<String> sourceDirectories = project.extensions.coveralls.sourceDirs

        def coverage = slurper.parse(file)
        List<SourceReport> reports = new ArrayList<SourceReport>()

        coverage.classCoverage.apexClasses.each { apexClass ->

            String sourceFilename = actualSourceFilename(sourceDirectories, (String)apexClass.name +".cls")

            if (sourceFilename == null) {
                // if sourceFilename is not found then ignore the entry
                return
            }

            File sourceFile = new File(sourceFilename)
            String source = sourceFile.text

            reports.add new SourceReport(sourceFilename, source, apexClass.coverage)

        }
        return reports
    }


    /**
     * finds the actual source file path and returns File object
     *
     * @param sourceDirs the list of candidate source dirs
     * @param filename the file name to search
     * @return found File object
     */
    private static String actualSourceFilename(List<String> sourceDirs, String filename) {
        println sourceDirs + " " + filename
        return sourceDirs.collect { it + '/' + filename }.find { new File(it).exists() }
    }


}
