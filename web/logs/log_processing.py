def formatLogFilePath(log_file_path):
    log_file_path = log_file_path + ".txt"
    return log_file_path

if __name__ == "__main__":
    print("Enter the log file you want to process...")
    log_file_path = formatLogFilePath(input(f"Log File: "))

    print(f"\nProcessing '{log_file_path}'...\n")
    try:
        log_file = open(log_file_path, "r")
        cumulative_TS = 0
        cumulative_TJ = 0
        count = 0

        for line in log_file:
            elapsedTS, elapsedTJ = line.split(",")
            cumulative_TS += float(elapsedTS)
            cumulative_TJ += float(elapsedTJ)
            count += 1

        print(f"+------------------{'-' * len(log_file_path)}--+")
        print(f"| Log Results for '{log_file_path}' |")
        print(f"+------------------{'-' * len(log_file_path)}--+")
        print(f"Average TS: {cumulative_TS / count} nanoseconds")
        print(f"Average TJ: {cumulative_TJ / count} nanoseconds")

        print()
        wipeLogFile = input(f"Do you want to wipe '{log_file_path}' (Y/N): ")
        if wipeLogFile.upper() == "Y":
            print(f"Wiping '{log_file_path}'...")
            open(log_file_path, "w")
            print(f"Success, '{log_file_path}' has been wiped")

        print("\nGoodbye")
    except IOError as e:
        print(f"\nError '{log_file_path}' doesn't exist...")
        print(e)


