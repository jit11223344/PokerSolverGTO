from setuptools import setup, find_packages

with open("README.md", "r", encoding="utf-8") as fh:
    long_description = fh.read()

setup(
    name="pokersolver-gto",
    version="0.1.0",
    author="PokerSolver Team",
    description="A Game Theory Optimal poker solver application",
    long_description=long_description,
    long_description_content_type="text/markdown",
    url="https://github.com/jit11223344/PokerSolverGTO",
    packages=find_packages(),
    classifiers=[
        "Programming Language :: Python :: 3",
        "License :: OSI Approved :: MIT License",
        "Operating System :: OS Independent",
    ],
    python_requires=">=3.7",
    install_requires=[
        "numpy>=1.19.0",
    ],
    entry_points={
        "console_scripts": [
            "pokersolver=pokersolver.cli:main",
        ],
    },
)
