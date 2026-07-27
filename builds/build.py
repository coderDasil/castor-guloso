#!/usr/bin/env python3
"""
Castor Guloso — Build Runner
Silent Maven compile, log to timestamped file, report result.
"""

import subprocess
import sys
import os
from datetime import datetime
from pathlib import Path

PROJECT_DIR = Path(__file__).resolve().parent.parent
BUILDS_DIR = PROJECT_DIR / "builds"
POM = PROJECT_DIR / "pom.xml"

BUILDS_DIR.mkdir(parents=True, exist_ok=True)


def timestamp():
    return datetime.now().strftime("%Y%m%d-%H%M%S")


def run_build() -> tuple[int, str]:
    """Run mvn clean compile. Returns (exit_code, log_path)."""
    log_name = f"build-{timestamp()}.log"
    log_path = BUILDS_DIR / log_name

    print(f"[build] Compiling...  log → {log_path}")

    with open(log_path, "w") as log:
        proc = subprocess.run(
            ["mvn", "clean", "compile", "--no-transfer-progress"],
            cwd=PROJECT_DIR,
            stdout=log,
            stderr=subprocess.STDOUT,
            text=True,
        )

    return proc.returncode, str(log_path)


def check_build(exit_code: int, log_path: str) -> bool:
    """Analyse build log and print summary. Returns True if OK."""
    success = exit_code == 0
    status = "✅ BUILD SUCCESS" if success else "❌ BUILD FAILURE"
    print(f"[build] Exit code: {exit_code}  |  {status}")
    print(f"[build] Log: {log_path}")

    if success:
        return True

    # Grep for ERROR / FAILURE / Warning highlights
    grep_patterns = ["ERROR", "FAILURE", "BUILD FAILURE", "Compilation failure"]
    issues = []

    with open(log_path) as f:
        for line in f:
            for pat in grep_patterns:
                if pat in line.upper() and pat.lower() in line.lower():
                    issues.append(line.rstrip())
                    break

    if issues:
        print(f"\n[build] 🔍 Found {len(issues)} failure-related lines (filtered):")
        for line in issues[:30]:  # cap output
            print(f"  {line}")
    else:
        print("\n[build] ⚠️  Build failed but no ERROR/FAILURE lines found in log (check manually).")

    return False


def main():
    exit_code, log_path = run_build()
    ok = check_build(exit_code, log_path)
    sys.exit(0 if ok else 1)


if __name__ == "__main__":
    main()
