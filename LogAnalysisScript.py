import os
from pathlib import Path


scriptDirectory = os.path.dirname(os.path.realpath(__file__))


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
            print("==================")
            print("=   Testsuite    =")
            print("==================")

    if("testcase" in logLine):
        data = logLine.split()
        print("-------------")
        print("- Testcase  -")
        print("-------------")

    if(data is not None):
        for info in data:
            if("name=" in info):
                if not ("hostname=" in info) and not ("classname=" in info):
                    print("Name: " + getData(info))
                if("classname=" in info):
                    print("Class name: " + getData(info))
            if("tests=" in info):
                print("# of tests: " + getData(info))
            if("skipped=" in info):
                print("# of skipped tests: " + getData(info))
            if("failures=" in info):
                print("# of failured tests: " + getData(info))
            if("errors=" in info):
                print("# of errors in tests: " + getData(info))
            if("time=" in info):
                print("Time: " + getData(info))


def getData(logLine):
    info = logLine.split("=")
    return info[1]


def getAppUnitTestLogs():

    print("***************************************************************************")
    print("*                           Unit Tests in App                             *")
    print("***************************************************************************")

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

    print("***************************************************************************")
    print("*                           Unit Tests in Core                            *")
    print("***************************************************************************")

    logDirectory = "/core/build/test-results/testFreeDebugUnitTest/"
    dirName = Path(scriptDirectory + logDirectory)
    listOfLogFiles = getListOfLogFiles(dirName)

    for logFile in listOfLogFiles:
        log = Path(scriptDirectory + logDirectory + logFile)
        file = open(log, "r")
        for line in file:
            getUnitTestData(line)
        file.close()


def getAppLintingLogs():

    print("***************************************************************************")
    print("*                           Lint Logs from App                            *")
    print("***************************************************************************")

    # Logs for app linting
    filename = Path(scriptDirectory + "/app/build/reports/lint-results-freeDebug.html")


def getCoreLintingLogs():

    print("***************************************************************************")
    print("*                           Lint Logs from Core                           *")
    print("***************************************************************************")

    # Logs for core linting
    filename = Path(scriptDirectory + "/core/build/reports/lint-results-freeDebug.html")


def main():
    getAppUnitTestLogs()
    getCoreUnitTestLogs()
    # getAppLintingLogs()
    # getCoreLintingLogs()


if __name__ == '__main__':
    main()
