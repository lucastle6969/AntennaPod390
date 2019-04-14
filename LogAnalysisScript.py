import os
import datetime
from datetime import date
from pathlib import Path

analysis = list()
scriptDirectory = os.path.dirname(os.path.realpath(__file__))


# Different headers for the analysis report output
def header1(title):
    global analysis
    analysis.append("***************************************************************************")
    analysis.append("                        " + title)
    analysis.append("***************************************************************************")


def header2(title):
    global analysis
    analysis.append("==================")
    analysis.append("  " + title)
    analysis.append("==================")


def header3(title):
    global analysis
    analysis.append("----------------")
    analysis.append("  " + title)
    analysis.append("----------------")


# Returns a list of all .xml files in the given directory
def getListOfLogFiles(dirName):

    listOfLogFiles = os.listdir(dirName)
    allLogFiles = list()

    for logFile in listOfLogFiles:
        if("xml" in logFile):
            allLogFiles.append(logFile)

    return allLogFiles


# # # Unit Test Reports

def getAppUnitTestLogs():
    global scriptDirectory

    header1("Unit Tests in App")

    logDirectory = "/app/build/test-results/testFreeDebugUnitTest/"
    dirName = Path(scriptDirectory + logDirectory)
    listOfLogFiles = getListOfLogFiles(dirName)

    for logFile in listOfLogFiles:
        log = Path(scriptDirectory + logDirectory + logFile)
        file = open(log, "r")
        for line in file:
            getUnitTestData(line)
        file.close()


def getCoreUnitTestLogs():
    global scriptDirectory

    header1("Unit Tests in Core")

    logDirectory = "/core/build/test-results/testFreeDebugUnitTest/"
    dirName = Path(scriptDirectory + logDirectory)
    listOfLogFiles = getListOfLogFiles(dirName)

    for logFile in listOfLogFiles:
        log = Path(scriptDirectory + logDirectory + logFile)
        file = open(log, "r")
        for line in file:
            getUnitTestData(line)
        file.close()


def getUnitTestData(logLine):
    global analysis
    data = None

    if("testsuite" in logLine):
        data = logLine.split()
        if not("/testsuite" in logLine):
            header2("Testsuite")
 
    if("testcase" in logLine):
        data = logLine.split()
        header3("Testcase")

    if(data is not None):
        for info in data:
            if("name=" in info):
                if not ("hostname=" in info) and not ("classname=" in info):
                    analysis.append("Name: " + getData(info))
                if("classname=" in info):
                    analysis.append("Class name: " + getData(info))
            if("tests=" in info):
                analysis.append("# of tests: " + getData(info))
            if("failures=" in info):
                analysis.append("# of failed tests: " + getData(info))
            if("errors=" in info):
                analysis.append("# of errors in tests: " + getData(info))

    if("failure" in logLine):
        data = logLine.split()
        for info in data:
            if("message=" in info):
                analysis.append("Reason of failure: " + getData(info))


def getData(logLine):
    info = logLine.split("=")
    return info[1]


# # # Linting Reports

def getAppLintingLogs():
    global scriptDirectory

    header1("Lint Logs from App")

    # Logs for app linting
    filename = Path(scriptDirectory + "/app/build/reports/lint-results-freeDebug.html")

    report = open(filename, "r")
    for line in report:
        getLintReportData(line)


def getCoreLintingLogs():
    global scriptDirectory

    header1("Lint Logs from Core")

    # Logs for core linting
    filename = Path(scriptDirectory + "/core/build/reports/lint-results-freeDebug.html")

    report = open(filename, "r", encoding="utf8")
    for line in report:
        getLintReportData(line)


def getLintReportData(line):
    if("mdl-layout-title" and "Lint Report:" in line):
            info = line.split(":")
            data = info[1]
            result = data[0:(len(data) - 8)]
            analysis.append(result)

    if("material-icons error-icon" in line):
        if("countColumn" not in line):
            info = line.split()
            href = info[2]
            data = href[7:(len(href) - 4)]
            analysis.append("Error: " + data)


# # # Coverage Reports

def getCoverageReport():
    global scriptDirectory

    missedLines = 0
    missedClasses = 0
    missedMethods = 0
    missedBranches = 0
    missedComplexity = 0

    coveredLines = 0
    coveredClasses = 0
    coveredMethods = 0
    coveredBranches = 0
    coveredComplexity = 0

    header1("Coverage Report")

    # Logs for core linting
    filename = Path(scriptDirectory + "/app/build/reports/jacoco/testFreeDebugUnitTestCoverage/testFreeDebugUnitTestCoverage.xml")

    report = open(filename, "r")
    reportSections = report.readline().split("/>")
    for info in reportSections:

        if("LINE" in info):
            data = getCoverageData(info)
            if(data is not None):
                missedLines += data[0]
                coveredLines += data[1]

        if("METHOD" in info):
            data = getCoverageData(info)
            if(data is not None):
                missedMethods += data[0]
                coveredMethods += data[1]

        if("CLASS" in info):
            data = getCoverageData(info)
            if(data is not None):
                missedClasses += data[0]
                coveredClasses += data[1]

        if("BRANCH" in info):
            data = getCoverageData(info)
            if(data is not None):
                missedBranches += data[0]
                coveredBranches += data[1]

        if("COMPLEXITY" in info):
            data = getCoverageData(info)
            if(data is not None):
                missedComplexity += data[0]
                coveredComplexity += data[1]

    header3("Lines")
    writeMissedCoverage(missedLines)
    writeCoveredCoverage(coveredLines)
    writePercentCoverage(missedLines, coveredLines)

    header3("Methods")
    writeMissedCoverage(missedMethods)
    writeCoveredCoverage(coveredMethods)
    writePercentCoverage(missedMethods, coveredMethods)

    header3("Classes")
    writeMissedCoverage(missedClasses)
    writeCoveredCoverage(coveredClasses)
    writePercentCoverage(missedClasses, coveredClasses)

    header3("Branches")
    writeMissedCoverage(missedBranches)
    writeCoveredCoverage(coveredBranches)
    writePercentCoverage(missedBranches, coveredBranches)

    header3("Complexity")
    writeMissedCoverage(missedComplexity)
    writeCoveredCoverage(coveredComplexity)
    writePercentCoverage(missedComplexity, coveredComplexity)


def writeMissedCoverage(missed):
    analysis.append("Missed: " + str(missed))


def writeCoveredCoverage(covered):
    analysis.append("Covered: " + str(covered))


def writePercentCoverage(missed, covered):
    percentage = 0
    if(missed != 0):
        percentage = (covered / missed) * 100
    analysis.append("Percent coverage: " + str(percentage) + " %")


def getCoverageData(info):

    missed = 0
    covered = 0
    stats = list()

    narrowInfo = info.split()

    for data in narrowInfo:

        if("missed" in data):
            result = getData(data)
            num = result[1:(len(result) - 1)]
            missed += int(num)

        if("covered" in data):
            result = getData(data)
            res = result.split('/')
            res2 = res[0]
            num = res2[1:(len(res2) - 1)]
            covered += int(num)

    stats.append(missed)
    stats.append(covered)

    return stats


def main():
    global analysis
    global scriptDirectory

    path = Path(scriptDirectory + "/logAnalysis/")
    if not os.path.exists(path):
        os.makedirs(path)

    getAppUnitTestLogs()
    getCoreUnitTestLogs()
    getAppLintingLogs()
    getCoreLintingLogs()
    getCoverageReport()

    os.chdir(path)

    now = datetime.datetime.now()
    today = date.today()
    time = now.strftime("%H-%M")
    result = open("logAnalysisResult-" + str(today) + "-at-" + time + ".txt", "w+")

    for line in analysis:
        result.write(line + "\n")

    result.close()


if __name__ == '__main__':
    main()
