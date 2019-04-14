import os
import datetime
from pathlib import Path


# Returns a list of all .xml files in the given directory
def getListOfLogFiles(dirName):

    listOfLogFiles = os.listdir(dirName)
    allLogFiles = list()

    for logFile in listOfLogFiles:
        if("xml" in logFile):
            allLogFiles.append(logFile)

    return allLogFiles


def getUnitTestData(logLine):
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
                    print("Name: " + getData(info))
                if("classname=" in info):
                    print("Class name: " + getData(info))
            if("tests=" in info):
                print("# of tests: " + getData(info))
            if("failures=" in info):
                print("# of failed tests: " + getData(info))
            if("errors=" in info):
                print("# of errors in tests: " + getData(info))
    
    if("failure" in logLine):
        data = logLine.split()
        for info in data:
            if("message=" in info):
                print("Reason of failure: " + getData(info))


def getData(logLine):
    info = logLine.split("=")
    return info[1]


def getAppUnitTestLogs(scriptDirectory):

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


def getCoreUnitTestLogs(scriptDirectory):

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


def getAppLintingLogs(scriptDirectory):

    header1("Lint Logs from App")

    # Logs for app linting
    filename = Path(scriptDirectory + "/app/build/reports/lint-results-freeDebug.html")

    report = open(filename, "r")
    for line in report:
        if("mdl-layout-title" and "Lint Report:" in line):
            print(line)


def getCoreLintingLogs(scriptDirectory):

    header1("Lint Logs from Core")

    # Logs for core linting
    filename = Path(scriptDirectory + "/core/build/reports/lint-results-freeDebug.html")

    report = open(filename, "r")
    for line in report:
        if("mdl-layout-title" and "Lint Report:" in line):
            print(line)


def getCoverageReport(scriptDirectory):
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
    print("Missed: " + str(missed))


def writeCoveredCoverage(covered):
    print("Covered: " + str(covered))


def writePercentCoverage(missed, covered):
    percentage = 0
    if(missed != 0):
        percentage = (covered / missed) * 100
    print("Percent coverage: " + str(percentage) + " % \n")


def header1(title):
    print("***************************************************************************")
    print("                        " + title)
    print("***************************************************************************")


def header2(title):
    print("==================")
    print("  " + title)
    print("==================")


def header3(title):
    print("----------------")
    print("  " + title)
    print("----------------")

# def writeToFile(logAnalysisResultLine):
#     result = write(logAnalysisResultLine + "\n")


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


def main(now, scriptDirectory):
    # path = Path(scriptDirectory + "/logAnalysis/")
    # if not os.path.exists(path):
    #     os.makedirs(path)
    # result = open("logAnalysisResult-" + str(now) + ".txt", "w+")
    # getAppUnitTestLogs()
    # getCoreUnitTestLogs()
    # getAppLintingLogs()
    # getCoreLintingLogs()
    getCoverageReport(scriptDirectory)
    # result.close()


if __name__ == '__main__':
    now = datetime.datetime.now()
    scriptDirectory = os.path.dirname(os.path.realpath(__file__))
    main(now, scriptDirectory)
