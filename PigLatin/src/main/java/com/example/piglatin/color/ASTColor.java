package com.example.piglatin.color;


import com.example.piglatin.analizador.gramatica.LatinusLexer;
import com.example.piglatin.analizador.gramatica.LatinusParser;
import com.example.piglatin.analizador.gramatica.LatinusParserBaseVisitor;
import com.example.piglatin.color.ColorMapa.TextoColoreado;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.List;

public class ASTColor extends LatinusParserBaseVisitor<List<ColorMapa.TextoColoreado>> {

    private final BufferedTokenStream tokens;

    public ASTColor(BufferedTokenStream tokens){
        this.tokens = tokens;
    }

    private List<ColorMapa.TextoColoreado> procesarToken(Token token, String tipoColor) {
        List<ColorMapa.TextoColoreado> lista = new ArrayList<>();
        if (token == null) return lista;

        //extraer el canal hidden del lexer
        List<Token> hiddenTokens = tokens.getHiddenTokensToLeft(token.getTokenIndex(), Lexer.HIDDEN);
        if (hiddenTokens != null) {
            for (Token ht : hiddenTokens) {
                if (ht.getType() == LatinusLexer.LINEA_COMENTARIO || ht.getType() == LatinusLexer.BLOQUE_COMENTARIO) {
                    lista.add(ColorMapa.colorear(ht.getText(), "COMENTARIO"));
                } else {
                    lista.add(ColorMapa.colorear(ht.getText(), "WS"));
                }
            }
        }

        lista.add(ColorMapa.colorear(token.getText(), tipoColor));
        return lista;
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitPrograma(LatinusParser.ProgramaContext ctx) {
        List<ColorMapa.TextoColoreado> resultado = new ArrayList<>();

        if (ctx.seccionVariables() != null) {
            resultado.addAll(visit(ctx.seccionVariables()));
        }
        if (ctx.seccionFunciones() != null) {
            resultado.addAll(visit(ctx.seccionFunciones()));
        }
        resultado.addAll(visit(ctx.seccionMain()));

        if (ctx.FIN_PROGRAMA() != null) {
            resultado.addAll(procesarToken(ctx.FIN_PROGRAMA().getSymbol(), "FIN_PROGRAMA"));
        }
        if (ctx.PUNTOCOMA() != null) {
            resultado.addAll(procesarToken(ctx.PUNTOCOMA().getSymbol(), "PUNTOCOMA"));
        }

        return resultado;
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitSeccionVariables(LatinusParser.SeccionVariablesContext ctx) {
        List<ColorMapa.TextoColoreado> resultado = new ArrayList<>();
        resultado.addAll(procesarToken(ctx.VARIABILES().getSymbol(), "VARIABILES"));
        resultado.addAll(procesarToken(ctx.MAYOR().getSymbol(), "MAYOR"));

        for (LatinusParser.DeclaracionVarContext decl : ctx.declaracionVar()) {
            resultado.addAll(visit(decl));
        }
        return resultado;
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override
    public List<ColorMapa.TextoColoreado> visitSeccionFunciones(LatinusParser.SeccionFuncionesContext ctx) {
        List<ColorMapa.TextoColoreado> resultado = new ArrayList<>();

        resultado.addAll(procesarToken(ctx.MUNERA().getSymbol(), "MUNERA"));
        resultado.addAll(procesarToken(ctx.MAYOR().getSymbol(), "MAYOR"));

        for (LatinusParser.FuncionContext func : ctx.funcion()) {
            resultado.addAll(visit(func));
        }

        return resultado;
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitSeccionMain(LatinusParser.SeccionMainContext ctx) {
        List<ColorMapa.TextoColoreado> resultado = new ArrayList<>();

        resultado.addAll(procesarToken(ctx.MAIOR().getSymbol(), "MAIOR"));
        resultado.addAll(procesarToken(ctx.MAYOR().getSymbol(), "MAYOR"));

        for (LatinusParser.SentenciaContext sent : ctx.sentencia()) {
            resultado.addAll(visit(sent));
        }

        return resultado;
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitDeclaracionVar(LatinusParser.DeclaracionVarContext ctx) {
        return visit(ctx.getChild(0));
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitVariablePrimitiva(LatinusParser.VariablePrimitivaContext ctx) {
        List<ColorMapa.TextoColoreado> resultado = new ArrayList<>();

        resultado.addAll(procesarToken(ctx.ESTO().getSymbol(), "ESTO"));
        resultado.addAll(procesarToken(ctx.ID().getSymbol(), "ID"));
        resultado.addAll(procesarToken(ctx.DOSPUNTOS().getSymbol(), "DOSPUNTOS"));
        resultado.addAll(visit(ctx.tipoPrimitivo()));

        if (ctx.expr() != null) {
            resultado.addAll(visit(ctx.expr()));
        }

        resultado.addAll(procesarToken(ctx.PUNTOCOMA().getSymbol(), "PUNTOCOMA"));

        return resultado;
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitVariableBooleana(LatinusParser.VariableBooleanaContext ctx) {
        List<ColorMapa.TextoColoreado> resultado = new ArrayList<>();

        resultado.addAll(procesarToken(ctx.ESTO().getSymbol(), "ESTO"));
        resultado.addAll(procesarToken(ctx.ID().getSymbol(), "ID"));
        resultado.addAll(procesarToken(ctx.DOSPUNTOS().getSymbol(), "DOSPUNTOS"));

        if (ctx.VERUM() != null) {
            resultado.addAll(procesarToken(ctx.VERUM().getSymbol(), "VERUM"));
        } else if (ctx.FALSUS() != null) {
            resultado.addAll(procesarToken(ctx.FALSUS().getSymbol(), "FALSUS"));
        }

        resultado.addAll(procesarToken(ctx.PUNTOCOMA().getSymbol(), "PUNTOCOMA"));

        return resultado;
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitTipoPrimitivo(LatinusParser.TipoPrimitivoContext ctx) {
        Token tokenTipo = ctx.getStart();
        return procesarToken(tokenTipo, tokenTipo.getText().toUpperCase());
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitTipo(LatinusParser.TipoContext ctx) {
        if (ctx.tipoPrimitivo() != null) {
            return visit(ctx.tipoPrimitivo());
        } else {
            return procesarToken(ctx.ID().getSymbol(), "ID");
        }
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitArregloTipado(LatinusParser.ArregloTipadoContext ctx) {
        List<ColorMapa.TextoColoreado> resultado = new ArrayList<>();

        resultado.addAll(procesarToken(ctx.SERIES().getSymbol(), "SERIES"));
        resultado.addAll(procesarToken(ctx.ID().getSymbol(), "ID"));
        resultado.addAll(procesarToken(ctx.CORCH_A().getSymbol(), "CORCH_A"));
        resultado.addAll(procesarToken(ctx.INT().getSymbol(), "INT"));
        resultado.addAll(procesarToken(ctx.CORCH_C().getSymbol(), "CORCH_C"));
        resultado.addAll(procesarToken(ctx.DOSPUNTOS().getSymbol(), "DOSPUNTOS"));
        resultado.addAll(visit(ctx.tipo()));

        if (ctx.listaExpr() != null) {
            resultado.addAll(procesarToken(ctx.LLAVE_A().getSymbol(), "LLAVE_A"));
            resultado.addAll(visit(ctx.listaExpr()));
            resultado.addAll(procesarToken(ctx.LLAVE_C().getSymbol(), "LLAVE_C"));
        }

        resultado.addAll(procesarToken(ctx.PUNTOCOMA().getSymbol(), "PUNTOCOMA"));

        return resultado;
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitArregloBooleano(LatinusParser.ArregloBooleanoContext ctx) {
        List<ColorMapa.TextoColoreado> resultado = new ArrayList<>();

        resultado.addAll(procesarToken(ctx.SERIES().getSymbol(), "SERIES"));
        resultado.addAll(procesarToken(ctx.ID().getSymbol(), "ID"));
        resultado.addAll(procesarToken(ctx.CORCH_A().getSymbol(), "CORCH_A"));
        resultado.addAll(procesarToken(ctx.INT().getSymbol(), "INT"));
        resultado.addAll(procesarToken(ctx.CORCH_C().getSymbol(), "CORCH_C"));
        resultado.addAll(procesarToken(ctx.DOSPUNTOS().getSymbol(), "DOSPUNTOS"));
        resultado.addAll(procesarToken(ctx.LLAVE_A().getSymbol(), "LLAVE_A"));
        resultado.addAll(visit(ctx.listaExpr()));
        resultado.addAll(procesarToken(ctx.LLAVE_C().getSymbol(), "LLAVE_C"));
        resultado.addAll(procesarToken(ctx.PUNTOCOMA().getSymbol(), "PUNTOCOMA"));

        return resultado;
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitListaExpr(LatinusParser.ListaExprContext ctx) {
        List<ColorMapa.TextoColoreado> resultado = new ArrayList<>();

        for (int i = 0; i < ctx.expr().size(); i++) {
            resultado.addAll(visit(ctx.expr(i)));
            // Procesamos la coma usando el token exacto registrado en la gramática
            if (i < ctx.COMA().size()) {
                resultado.addAll(procesarToken(ctx.COMA(i).getSymbol(), "COMA"));
            }
        }

        return resultado;
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitStructDef(LatinusParser.StructDefContext ctx) {
        List<ColorMapa.TextoColoreado> resultado = new ArrayList<>();

        resultado.addAll(procesarToken(ctx.STRUCTURA().getSymbol(), "STRUCTURA"));
        resultado.addAll(procesarToken(ctx.ID().getSymbol(), "ID"));
        resultado.addAll(procesarToken(ctx.LLAVE_A().getSymbol(), "LLAVE_A"));

        for (LatinusParser.CampoStructContext campo : ctx.campoStruct()) {
            resultado.addAll(visit(campo));
        }

        resultado.addAll(procesarToken(ctx.LLAVE_C().getSymbol(), "LLAVE_C"));
        resultado.addAll(procesarToken(ctx.FINIS().getSymbol(), "FINIS"));
        resultado.addAll(procesarToken(ctx.PUNTOCOMA().getSymbol(), "PUNTOCOMA"));

        return resultado;
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitCampoStruct(LatinusParser.CampoStructContext ctx) {
        List<ColorMapa.TextoColoreado> resultado = new ArrayList<>();

        resultado.addAll(procesarToken(ctx.ESTO().getSymbol(), "ESTO"));
        resultado.addAll(procesarToken(ctx.ID().getSymbol(), "ID"));
        resultado.addAll(procesarToken(ctx.DOSPUNTOS().getSymbol(), "DOSPUNTOS"));
        resultado.addAll(visit(ctx.tipo()));
        resultado.addAll(procesarToken(ctx.PUNTOCOMA().getSymbol(), "PUNTOCOMA"));

        return resultado;
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitStructInstancia(LatinusParser.StructInstanciaContext ctx) {
        List<ColorMapa.TextoColoreado> resultado = new ArrayList<>();

        resultado.addAll(procesarToken(ctx.ESTO().getSymbol(), "ESTO"));
        resultado.addAll(procesarToken(ctx.ID(0).getSymbol(), "ID"));
        resultado.addAll(procesarToken(ctx.DOSPUNTOS().getSymbol(), "DOSPUNTOS"));
        resultado.addAll(procesarToken(ctx.ID(1).getSymbol(), "ID"));
        resultado.addAll(visit(ctx.literalStruct()));

        return resultado;
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitLiteralStruct(LatinusParser.LiteralStructContext ctx) {
        List<ColorMapa.TextoColoreado> resultado = new ArrayList<>();

        resultado.addAll(procesarToken(ctx.LLAVE_A().getSymbol(), "LLAVE_A"));

        for (int i = 0; i < ctx.asignacionCampo().size(); i++) {
            resultado.addAll(visit(ctx.asignacionCampo(i)));
            if (i < ctx.COMA().size()) {
                resultado.addAll(procesarToken(ctx.COMA(i).getSymbol(), "COMA"));
            }
        }

        resultado.addAll(procesarToken(ctx.LLAVE_C().getSymbol(), "LLAVE_C"));

        return resultado;
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitAsignacionCampo(LatinusParser.AsignacionCampoContext ctx) {
        List<ColorMapa.TextoColoreado> resultado = new ArrayList<>();

        resultado.addAll(procesarToken(ctx.ID().getSymbol(), "ID"));
        resultado.addAll(procesarToken(ctx.DOSPUNTOS().getSymbol(), "DOSPUNTOS"));
        resultado.addAll(visit(ctx.expr()));

        return resultado;
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitFuncionSinRetorno(LatinusParser.FuncionSinRetornoContext ctx) {
        List<ColorMapa.TextoColoreado> resultado = new ArrayList<>();

        resultado.addAll(procesarToken(ctx.ACTIO().getSymbol(), "ACTIO"));
        resultado.addAll(procesarToken(ctx.ID().getSymbol(), "ID"));
        resultado.addAll(procesarToken(ctx.PAR_A().getSymbol(), "PAR_A"));

        if (ctx.listaParametros() != null) {
            resultado.addAll(visit(ctx.listaParametros()));
        }

        resultado.addAll(procesarToken(ctx.PAR_C().getSymbol(), "PAR_C"));
        resultado.addAll(procesarToken(ctx.LLAVE_A().getSymbol(), "LLAVE_A"));

        if (ctx.bloqueVariables() != null) {
            resultado.addAll(visit(ctx.bloqueVariables()));
        }

        for (LatinusParser.SentenciaContext sent : ctx.sentencia()) {
            resultado.addAll(visit(sent));
        }

        resultado.addAll(procesarToken(ctx.LLAVE_C().getSymbol(), "LLAVE_C"));
        resultado.addAll(procesarToken(ctx.FINIS().getSymbol(), "FINIS"));
        resultado.addAll(procesarToken(ctx.PUNTOCOMA().getSymbol(), "PUNTOCOMA"));

        return resultado;
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitFuncionConRetorno(LatinusParser.FuncionConRetornoContext ctx) {
        List<ColorMapa.TextoColoreado> resultado = new ArrayList<>();

        resultado.addAll(procesarToken(ctx.RATIO().getSymbol(), "RATIO"));
        resultado.addAll(visit(ctx.tipo()));
        resultado.addAll(procesarToken(ctx.ID().getSymbol(), "ID"));
        resultado.addAll(procesarToken(ctx.PAR_A().getSymbol(), "PAR_A"));

        if (ctx.listaParametros() != null) {
            resultado.addAll(visit(ctx.listaParametros()));
        }

        resultado.addAll(procesarToken(ctx.PAR_C().getSymbol(), "PAR_C"));
        resultado.addAll(procesarToken(ctx.LLAVE_A().getSymbol(), "LLAVE_A"));

        if (ctx.bloqueVariables() != null) {
            resultado.addAll(visit(ctx.bloqueVariables()));
        }

        for (LatinusParser.SentenciaContext sent : ctx.sentencia()) {
            resultado.addAll(visit(sent));
        }

        resultado.addAll(procesarToken(ctx.LLAVE_C().getSymbol(), "LLAVE_C"));
        resultado.addAll(procesarToken(ctx.FINIS().getSymbol(), "FINIS"));
        resultado.addAll(procesarToken(ctx.PUNTOCOMA().getSymbol(), "PUNTOCOMA"));

        return resultado;
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitListaParametros(LatinusParser.ListaParametrosContext ctx) {
        List<ColorMapa.TextoColoreado> resultado = new ArrayList<>();

        for (int i = 0; i < ctx.parametro().size(); i++) {
            resultado.addAll(visit(ctx.parametro(i)));
            if (i < ctx.COMA().size()) {
                resultado.addAll(procesarToken(ctx.COMA(i).getSymbol(), "COMA"));
            }
        }

        return resultado;
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitParametro(LatinusParser.ParametroContext ctx) {
        List<ColorMapa.TextoColoreado> resultado = new ArrayList<>();

        resultado.addAll(procesarToken(ctx.ESTO().getSymbol(), "ESTO"));
        resultado.addAll(procesarToken(ctx.ID().getSymbol(), "ID"));
        resultado.addAll(procesarToken(ctx.DOSPUNTOS().getSymbol(), "DOSPUNTOS"));
        resultado.addAll(visit(ctx.tipo()));

        return resultado;
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitBloqueVariables(LatinusParser.BloqueVariablesContext ctx) {
        List<ColorMapa.TextoColoreado> resultado = new ArrayList<>();

        resultado.addAll(procesarToken(ctx.VARIABILES().getSymbol(), "VARIABILES"));
        resultado.addAll(procesarToken(ctx.CORCH_A().getSymbol(), "CORCH_A"));

        for (LatinusParser.DeclaracionVarContext decl : ctx.declaracionVar()) {
            resultado.addAll(visit(decl));
        }

        resultado.addAll(procesarToken(ctx.CORCH_C().getSymbol(), "CORCH_C"));

        return resultado;
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitSentencia(LatinusParser.SentenciaContext ctx) {
        return visit(ctx.getChild(0));
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitIncrementoDecremento(LatinusParser.IncrementoDecrementoContext ctx) {
        List<ColorMapa.TextoColoreado> resultado = new ArrayList<>();

        boolean esSufijo = ctx.referencia() != null &&
                (ctx.INC() != null || ctx.DEC() != null) &&
                ctx.referencia().getStart().getTokenIndex() <
                        (ctx.INC() != null ? ctx.INC().getSymbol().getTokenIndex() : ctx.DEC().getSymbol().getTokenIndex());

        if (esSufijo) {
            resultado.addAll(visit(ctx.referencia()));
            if (ctx.INC() != null) {
                resultado.addAll(procesarToken(ctx.INC().getSymbol(), "INC"));
            } else if (ctx.DEC() != null) {
                resultado.addAll(procesarToken(ctx.DEC().getSymbol(), "DEC"));
            }
        } else {
            if (ctx.INC() != null) {
                resultado.addAll(procesarToken(ctx.INC().getSymbol(), "INC"));
            } else if (ctx.DEC() != null) {
                resultado.addAll(procesarToken(ctx.DEC().getSymbol(), "DEC"));
            }
            resultado.addAll(visit(ctx.referencia()));
        }

        resultado.addAll(procesarToken(ctx.PUNTOCOMA().getSymbol(), "PUNTOCOMA"));

        return resultado;
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitAsignacion(LatinusParser.AsignacionContext ctx) {
        List<ColorMapa.TextoColoreado> resultado = new ArrayList<>();

        resultado.addAll(visit(ctx.referencia()));
        resultado.addAll(procesarToken(ctx.ASIGNAR().getSymbol(), "ASIGNAR"));
        resultado.addAll(visit(ctx.expr()));
        resultado.addAll(procesarToken(ctx.PUNTOCOMA().getSymbol(), "PUNTOCOMA"));

        return resultado;
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitAsignacionStructLiteral(LatinusParser.AsignacionStructLiteralContext ctx) {
        List<ColorMapa.TextoColoreado> resultado = new ArrayList<>();

        resultado.addAll(visit(ctx.referencia()));
        resultado.addAll(procesarToken(ctx.ASIGNAR().getSymbol(), "ASIGNAR"));
        resultado.addAll(visit(ctx.literalStruct()));

        return resultado;
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitAccesoAtributo(LatinusParser.AccesoAtributoContext ctx) {
        List<ColorMapa.TextoColoreado> resultado = new ArrayList<>();

        resultado.addAll(visit(ctx.referencia()));
        resultado.addAll(procesarToken(ctx.PUNTO().getSymbol(), "PUNTO"));
        resultado.addAll(procesarToken(ctx.ID().getSymbol(), "ID"));

        return resultado;
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitAccesoArray(LatinusParser.AccesoArrayContext ctx) {
        List<ColorMapa.TextoColoreado> resultado = new ArrayList<>();

        resultado.addAll(visit(ctx.referencia()));
        resultado.addAll(procesarToken(ctx.CORCH_A().getSymbol(), "CORCH_A"));
        resultado.addAll(visit(ctx.expr()));
        resultado.addAll(procesarToken(ctx.CORCH_C().getSymbol(), "CORCH_C"));

        return resultado;
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitReferenciaBase(LatinusParser.ReferenciaBaseContext ctx) {
        return procesarToken(ctx.ID().getSymbol(), "ID");
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitCondicional(LatinusParser.CondicionalContext ctx) {
        List<ColorMapa.TextoColoreado> resultado = new ArrayList<>();

        resultado.addAll(procesarToken(ctx.SI().getSymbol(), "SI"));
        resultado.addAll(procesarToken(ctx.PAR_A().getSymbol(), "PAR_A"));
        resultado.addAll(visit(ctx.expr()));
        resultado.addAll(procesarToken(ctx.PAR_C().getSymbol(), "PAR_C"));
        resultado.addAll(visit(ctx.bloqueSentencias()));

        for (LatinusParser.RamaAliterContext rama : ctx.ramaAliter()) {
            resultado.addAll(visit(rama));
        }

        if (ctx.ramaElse() != null) {
            resultado.addAll(visit(ctx.ramaElse()));
        }

        resultado.addAll(procesarToken(ctx.FINIS().getSymbol(), "FINIS"));
        resultado.addAll(procesarToken(ctx.PUNTOCOMA().getSymbol(), "PUNTOCOMA"));

        return resultado;
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitBloqueSentencias(LatinusParser.BloqueSentenciasContext ctx) {
        List<ColorMapa.TextoColoreado> resultado = new ArrayList<>();

        resultado.addAll(procesarToken(ctx.LLAVE_A().getSymbol(), "LLAVE_A"));

        for (LatinusParser.SentenciaContext sent : ctx.sentencia()) {
            resultado.addAll(visit(sent));
        }

        resultado.addAll(procesarToken(ctx.LLAVE_C().getSymbol(), "LLAVE_C"));

        return resultado;
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitRamaAliter(LatinusParser.RamaAliterContext ctx) {
        List<ColorMapa.TextoColoreado> resultado = new ArrayList<>();

        resultado.addAll(procesarToken(ctx.ALITER().getSymbol(), "ALITER"));
        resultado.addAll(procesarToken(ctx.PAR_A().getSymbol(), "PAR_A"));
        resultado.addAll(visit(ctx.expr()));
        resultado.addAll(procesarToken(ctx.PAR_C().getSymbol(), "PAR_C"));
        resultado.addAll(visit(ctx.bloqueSentencias()));

        return resultado;
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitRamaElse(LatinusParser.RamaElseContext ctx) {
        List<ColorMapa.TextoColoreado> resultado = new ArrayList<>();

        resultado.addAll(procesarToken(ctx.ALITER().getSymbol(), "ALITER"));
        resultado.addAll(visit(ctx.bloqueSentencias()));

        return resultado;
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitCicloDum(LatinusParser.CicloDumContext ctx) {
        List<ColorMapa.TextoColoreado> resultado = new ArrayList<>();

        resultado.addAll(procesarToken(ctx.DUM().getSymbol(), "DUM"));
        resultado.addAll(procesarToken(ctx.PAR_A().getSymbol(), "PAR_A"));
        resultado.addAll(visit(ctx.expr()));
        resultado.addAll(procesarToken(ctx.PAR_C().getSymbol(), "PAR_C"));
        resultado.addAll(procesarToken(ctx.LLAVE_A().getSymbol(), "LLAVE_A"));

        for (LatinusParser.SentenciaContext sent : ctx.sentencia()) {
            resultado.addAll(visit(sent));
        }

        resultado.addAll(procesarToken(ctx.LLAVE_C().getSymbol(), "LLAVE_C"));
        resultado.addAll(procesarToken(ctx.FINIS().getSymbol(), "FINIS"));
        resultado.addAll(procesarToken(ctx.PUNTOCOMA().getSymbol(), "PUNTOCOMA"));

        return resultado;
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitCicloFacere(LatinusParser.CicloFacereContext ctx) {
        List<ColorMapa.TextoColoreado> resultado = new ArrayList<>();

        resultado.addAll(procesarToken(ctx.FACERE().getSymbol(), "FACERE"));
        resultado.addAll(procesarToken(ctx.LLAVE_A().getSymbol(), "LLAVE_A"));

        for (LatinusParser.SentenciaContext sent : ctx.sentencia()) {
            resultado.addAll(visit(sent));
        }

        resultado.addAll(procesarToken(ctx.LLAVE_C().getSymbol(), "LLAVE_C"));
        resultado.addAll(procesarToken(ctx.DUM().getSymbol(), "DUM"));
        resultado.addAll(procesarToken(ctx.PAR_A().getSymbol(), "PAR_A"));
        resultado.addAll(visit(ctx.expr()));
        resultado.addAll(procesarToken(ctx.PAR_C().getSymbol(), "PAR_C"));
        resultado.addAll(procesarToken(ctx.PUNTOCOMA().getSymbol(), "PUNTOCOMA"));

        return resultado;
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitCicloPer(LatinusParser.CicloPerContext ctx) {
        List<ColorMapa.TextoColoreado> resultado = new ArrayList<>();

        resultado.addAll(procesarToken(ctx.PER().getSymbol(), "PER"));
        resultado.addAll(procesarToken(ctx.PAR_A().getSymbol(), "PAR_A"));

        // visit(variable) ya procesa su propio PUNTOCOMA interno
        resultado.addAll(visit(ctx.variable()));

        resultado.addAll(visit(ctx.expr()));

        resultado.addAll(procesarToken(ctx.PUNTOCOMA().getSymbol(), "PUNTOCOMA"));
        resultado.addAll(visit(ctx.incremento()));
        resultado.addAll(procesarToken(ctx.PAR_C().getSymbol(), "PAR_C"));
        resultado.addAll(procesarToken(ctx.LLAVE_A().getSymbol(), "LLAVE_A"));

        for (LatinusParser.SentenciaContext sent : ctx.sentencia()) {
            resultado.addAll(visit(sent));
        }

        resultado.addAll(procesarToken(ctx.LLAVE_C().getSymbol(), "LLAVE_C"));

        return resultado;
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitIncremento(LatinusParser.IncrementoContext ctx) {
        List<ColorMapa.TextoColoreado> resultado = new ArrayList<>();

        if (ctx.referencia() != null) {
            resultado.addAll(visit(ctx.referencia()));
            if (ctx.INC() != null) {
                resultado.addAll(procesarToken(ctx.INC().getSymbol(), "INC"));
            } else if (ctx.DEC() != null) {
                resultado.addAll(procesarToken(ctx.DEC().getSymbol(), "DEC"));
            } else if (ctx.ASIGNAR() != null) {
                resultado.addAll(procesarToken(ctx.ASIGNAR().getSymbol(), "ASIGNAR"));
                resultado.addAll(visit(ctx.expr()));
            }
        }

        return resultado;
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitInterrupcionCiclo(LatinusParser.InterrupcionCicloContext ctx) {
        List<ColorMapa.TextoColoreado> resultado = new ArrayList<>();

        if (ctx.PERGE() != null) {
            resultado.addAll(procesarToken(ctx.PERGE().getSymbol(), "PERGE"));
        } else if (ctx.INTERRUMPE() != null) {
            resultado.addAll(procesarToken(ctx.INTERRUMPE().getSymbol(), "INTERRUMPE"));
        }

        resultado.addAll(procesarToken(ctx.PUNTOCOMA().getSymbol(), "PUNTOCOMA"));

        return resultado;
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitRetorno(LatinusParser.RetornoContext ctx) {
        List<ColorMapa.TextoColoreado> resultado = new ArrayList<>();

        resultado.addAll(procesarToken(ctx.REDDERE().getSymbol(), "REDDERE"));
        resultado.addAll(visit(ctx.expr()));
        resultado.addAll(procesarToken(ctx.PUNTOCOMA().getSymbol(), "PUNTOCOMA"));

        return resultado;
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitLectura(LatinusParser.LecturaContext ctx) {
        List<ColorMapa.TextoColoreado> resultado = new ArrayList<>();

        if (ctx.ID() != null) {
            resultado.addAll(procesarToken(ctx.ID().getSymbol(), "ID"));
        }

        resultado.addAll(procesarToken(ctx.LEER().getSymbol(), "LEER"));

        return resultado;
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitEscritura(LatinusParser.EscrituraContext ctx) {
        List<ColorMapa.TextoColoreado> resultado = new ArrayList<>();

        for (int i = 0; i < ctx.expr().size(); i++) {
            resultado.addAll(procesarToken(ctx.ESCRIBIR(i).getSymbol(), "ESCRIBIR"));
            resultado.addAll(visit(ctx.expr(i)));
        }

        resultado.addAll(procesarToken(ctx.PUNTOCOMA().getSymbol(), "PUNTOCOMA"));

        return resultado;
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitLlamadaFuncion(LatinusParser.LlamadaFuncionContext ctx) {
        List<ColorMapa.TextoColoreado> resultado = new ArrayList<>();

        resultado.addAll(procesarToken(ctx.ID().getSymbol(), "ID"));
        resultado.addAll(procesarToken(ctx.PAR_A().getSymbol(), "PAR_A"));

        if (ctx.listaArgumentos() != null) {
            resultado.addAll(visit(ctx.listaArgumentos()));
        }

        resultado.addAll(procesarToken(ctx.PAR_C().getSymbol(), "PAR_C"));

        return resultado;
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitListaArgumentos(LatinusParser.ListaArgumentosContext ctx) {
        List<ColorMapa.TextoColoreado> resultado = new ArrayList<>();

        for (int i = 0; i < ctx.expr().size(); i++) {
            resultado.addAll(visit(ctx.expr(i)));
            if (i < ctx.expr().size() - 1) {
                resultado.addAll(procesarToken(ctx.COMA(i).getSymbol(), "COMA"));
            }
        }

        return resultado;
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitExprFalsus(LatinusParser.ExprFalsusContext ctx) {
        List<ColorMapa.TextoColoreado> resultado = new ArrayList<>();
        resultado.addAll(procesarToken(ctx.FALSUS().getSymbol(), "FALSUS"));
        return resultado;
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitExprIncDecPostfijo(LatinusParser.ExprIncDecPostfijoContext ctx) {
        List<ColorMapa.TextoColoreado> resultado = new ArrayList<>();
        resultado.addAll(procesarToken(ctx.ID().getSymbol(), "ID"));
        if (ctx.INC() != null) {
            resultado.addAll(procesarToken(ctx.INC().getSymbol(), "INC"));
        } else if (ctx.DEC() != null) {
            resultado.addAll(procesarToken(ctx.DEC().getSymbol(), "DEC"));
        }
        return resultado;
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitExprRelacional(LatinusParser.ExprRelacionalContext ctx) {
        List<ColorMapa.TextoColoreado> resultado = new ArrayList<>();
        resultado.addAll(visit(ctx.expr(0)));
        resultado.addAll(procesarToken(ctx.op, ctx.op.getText()));
        resultado.addAll(visit(ctx.expr(1)));
        return resultado;
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitExprParentesis(LatinusParser.ExprParentesisContext ctx) {
        List<ColorMapa.TextoColoreado> resultado = new ArrayList<>();
        resultado.addAll(procesarToken(ctx.PAR_A().getSymbol(), "PAR_A"));
        resultado.addAll(visit(ctx.expr()));
        resultado.addAll(procesarToken(ctx.PAR_C().getSymbol(), "PAR_C"));
        return resultado;
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitExprIncDecPrefijo(LatinusParser.ExprIncDecPrefijoContext ctx) {
        List<ColorMapa.TextoColoreado> resultado = new ArrayList<>();
        if (ctx.INC() != null) {
            resultado.addAll(procesarToken(ctx.INC().getSymbol(), "INC"));
        } else if (ctx.DEC() != null) {
            resultado.addAll(procesarToken(ctx.DEC().getSymbol(), "DEC"));
        }
        resultado.addAll(procesarToken(ctx.ID().getSymbol(), "ID"));
        return resultado;
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitExprLlamada(LatinusParser.ExprLlamadaContext ctx) {
        return visit(ctx.llamadaFuncion());
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitExprSumaResta(LatinusParser.ExprSumaRestaContext ctx) {
        List<ColorMapa.TextoColoreado> resultado = new ArrayList<>();
        resultado.addAll(visit(ctx.expr(0)));
        resultado.addAll(procesarToken(ctx.op, ctx.op.getText()));
        resultado.addAll(visit(ctx.expr(1)));
        return resultado;
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitExprOr(LatinusParser.ExprOrContext ctx) {
        List<ColorMapa.TextoColoreado> resultado = new ArrayList<>();
        resultado.addAll(visit(ctx.expr(0)));
        resultado.addAll(procesarToken(ctx.OR().getSymbol(), "OR"));
        resultado.addAll(visit(ctx.expr(1)));
        return resultado;
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitExprTexto(LatinusParser.ExprTextoContext ctx) {
        List<ColorMapa.TextoColoreado> resultado = new ArrayList<>();
        resultado.addAll(procesarToken(ctx.STRING().getSymbol(), "STRING"));
        return resultado;
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitExprNegacion(LatinusParser.ExprNegacionContext ctx) {
        List<ColorMapa.TextoColoreado> resultado = new ArrayList<>();
        resultado.addAll(procesarToken(ctx.NON().getSymbol(), "NON"));
        resultado.addAll(visit(ctx.expr()));
        return resultado;
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitExprEntero(LatinusParser.ExprEnteroContext ctx) {
        List<ColorMapa.TextoColoreado> resultado = new ArrayList<>();
        resultado.addAll(procesarToken(ctx.INT().getSymbol(), "INT"));
        return resultado;
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitExprIgualdad(LatinusParser.ExprIgualdadContext ctx) {
        List<ColorMapa.TextoColoreado> resultado = new ArrayList<>();
        resultado.addAll(visit(ctx.expr(0)));
        resultado.addAll(procesarToken(ctx.op, ctx.op.getText()));
        resultado.addAll(visit(ctx.expr(1)));
        return resultado;
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitExprAnd(LatinusParser.ExprAndContext ctx) {
        List<ColorMapa.TextoColoreado> resultado = new ArrayList<>();
        resultado.addAll(visit(ctx.expr(0)));
        resultado.addAll(procesarToken(ctx.AND().getSymbol(), "AND"));
        resultado.addAll(visit(ctx.expr(1)));
        return resultado;
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitExprVerum(LatinusParser.ExprVerumContext ctx) {
        List<ColorMapa.TextoColoreado> resultado = new ArrayList<>();
        resultado.addAll(procesarToken(ctx.VERUM().getSymbol(), "VERUM"));
        return resultado;
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitExprReferencia(LatinusParser.ExprReferenciaContext ctx) {
        return visit(ctx.referencia());
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitExprMulDiv(LatinusParser.ExprMulDivContext ctx) {
        List<ColorMapa.TextoColoreado> resultado = new ArrayList<>();
        resultado.addAll(visit(ctx.expr(0)));
        resultado.addAll(procesarToken(ctx.op, ctx.op.getText()));
        resultado.addAll(visit(ctx.expr(1)));
        return resultado;
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitExprCaracter(LatinusParser.ExprCaracterContext ctx) {
        List<ColorMapa.TextoColoreado> resultado = new ArrayList<>();
        resultado.addAll(procesarToken(ctx.CHAR().getSymbol(), "CHAR"));
        return resultado;
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitExprDecimal(LatinusParser.ExprDecimalContext ctx) {
        List<ColorMapa.TextoColoreado> resultado = new ArrayList<>();
        resultado.addAll(procesarToken(ctx.FLOAT().getSymbol(), "FLOAT"));
        return resultado;
    }
}
