import argostranslate.package
import argostranslate.translate
import pathlib
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, Field

from_code = "fr"
to_code = "en"

# install > pip install fastapi uvicorn argostranslate
# run > uvicorn main:app --reload

for path in pathlib.Path("./models").glob("*.argosmodel"):
    argostranslate.package.install_from_path(path)

app = FastAPI(title="Argos Translate Server")

class TranslateRequest(BaseModel):
    from_code: str = Field(..., examples=["fr"])
    to_code: str = Field(..., examples=["en"])
    text: str = Field(..., min_length=1, examples=["J'ai besoin d'aller aux toilettes"])

class TranslateResponse(BaseModel):
    translated_text: str

@app.post("/translate", response_model=TranslateResponse)
def translate(req: TranslateRequest):
    try:
        out = argostranslate.translate.translate(req.text, req.from_code, req.to_code)
    except Exception as e:
        raise HTTPException(status_code=400, detail=f"Traduction impossible: {e}")
    return TranslateResponse(translated_text=out)