## Installation

- Install Python (tested with [3.11.9](https://www.python.org/downloads/release/python-3119/))
- Install [VC++v14](https://aka.ms/vc14/vc_redist.x64.exe) (tested with [14.50.35719](https://learn.microsoft.com/en-us/cpp/windows/latest-supported-vc-redist?view=msvc-170))
- Install python dependencies `pip install -r requirements.txt`

## Run in dev

- Run  `python translation-server.py ./models/ --port 8000`

## Run LifeCompanion

- Set `LIFECOMPANION_TRANSLATION_SERVER_FOLDER` env var to python server app