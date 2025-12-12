import argparse
import pathlib

import argostranslate.package
import argostranslate.translate
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, Field
import uvicorn

app = FastAPI(title="Translate Server")

class TranslateRequest(BaseModel):
    from_code: str = Field(..., examples=["fr"])
    to_code: str = Field(..., examples=["en"])
    text: str = Field(..., min_length=1, examples=["Salut, comment ça va ?"])


class TranslateResponse(BaseModel):
    translated_text: str


def load_models(models_dir: pathlib.Path) -> None:
    if not models_dir.exists() or not models_dir.is_dir():
        raise RuntimeError(f"Le dossier des modèles n'existe pas: {models_dir}")

    for path in models_dir.glob("*.argosmodel"):
        print("Loading translation model from "+str(path))
        argostranslate.package.install_from_path(path)
    # print("Available packages "+str(argostranslate.package.get_available_packages()))
    


@app.post("/translate", response_model=TranslateResponse)
def translate(req: TranslateRequest):
    try:
        out = argostranslate.translate.translate(req.text, req.from_code, req.to_code)
    except Exception as e:
        raise HTTPException(status_code=400, detail=f"Traduction impossible: {e}")
    return TranslateResponse(translated_text=out)


def main() -> None:
    parser = argparse.ArgumentParser(description="Serveur de traduction")
    parser.add_argument(
        "models_dir",
        help="Dossier contenant les fichiers *.argosmodel",
    )
    parser.add_argument(
        "--host",
        default="0.0.0.0",
        help="Adresse d'écoute (par défaut: 0.0.0.0)",
    )
    parser.add_argument(
        "--port",
        "-p",
        type=int,
        default=8000,
        help="Port du serveur (par défaut: 8000)",
    )

    args = parser.parse_args()

    load_models(pathlib.Path(args.models_dir))

    uvicorn.run(app, host=args.host, port=args.port)

if __name__ == "__main__":
    main()
