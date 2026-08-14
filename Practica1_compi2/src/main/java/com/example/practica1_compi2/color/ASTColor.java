package com.example.practica1_compi2.color;

import com.example.practica1_compi2.analizador.ast.NodoAST;
import com.example.practica1_compi2.analizador.gramatica.LatinusParser;
import com.example.practica1_compi2.analizador.gramatica.LatinusParserBaseVisitor;
import com.example.practica1_compi2.color.ColorMapa.TextoColoreado;

import java.util.ArrayList;
import java.util.List;

public class ASTColor extends LatinusParserBaseVisitor<List<ColorMapa.TextoColoreado>>  {

    @Override
    public List<TextoColoreado> visitPrograma(LatinusParser.ProgramaContext ctx) {
        List<ColorMapa.TextoColoreado> resultado = new ArrayList<>();

        if (ctx.seccionVariables() != null) {
            resultado.addAll(visit(ctx.seccionVariables()));
        }
        if (ctx.seccionFunciones() != null) {
            resultado.addAll(visit(ctx.seccionFunciones()));
        }
        resultado.addAll(visit(ctx.seccionMain()));
        resultado.add(ColorMapa.colorear("FIN_PROGRAMA", "FIN_PROGRAMA"));
        resultado.add(ColorMapa.colorear(";", "PUNTOCOMA"));

        return resultado;
    }

    @Override
    public List<ColorMapa.TextoColoreado> visitSeccionVariables(LatinusParser.SeccionVariablesContext ctx) {
        List<ColorMapa.TextoColoreado> resultado = new ArrayList<>();

        resultado.add(ColorMapa.colorear("VARIABILES", "VARIABILES"));
        resultado.add(ColorMapa.colorear(">", "MAYOR"));

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
    public List<TextoColoreado> visitSeccionFunciones(LatinusParser.SeccionFuncionesContext ctx) {
        List<TextoColoreado> resultado = new ArrayList<>();

        resultado.add(ColorMapa.colorear("MUNERA", "MUNERA"));
        resultado.add(ColorMapa.colorear(">", "MAYOR"));

        for (LatinusParser.FuncionContext func : ctx.funcion()) {
            resultado.addAll(visit(func));
        }

        return resultado;
    }

    @Override
    public List<TextoColoreado> visitSeccionMain(LatinusParser.SeccionMainContext ctx) {
        List<TextoColoreado> resultado = new ArrayList<>();

        resultado.add(ColorMapa.colorear("MAIOR", "MAIOR"));
        resultado.add(ColorMapa.colorear(">", "MAYOR"));

        for (LatinusParser.SentenciaContext sent : ctx.sentencia()) {
            resultado.addAll(visit(sent));
        }

        return resultado;
    }

    @Override
    public List<TextoColoreado> visitDeclaracionVar(LatinusParser.DeclaracionVarContext ctx) {
        return visit(ctx.getChild(0));
    }

    @Override
    public List<TextoColoreado> visitVariablePrimitiva(LatinusParser.VariablePrimitivaContext ctx) {
        List<TextoColoreado> resultado = new ArrayList<>();

        resultado.add(ColorMapa.colorear("ESTO", "ESTO"));
        resultado.add(new TextoColoreado(" ", null));
        resultado.add(ColorMapa.colorear(ctx.ID().getText(), "ID"));
        resultado.add(ColorMapa.colorear(":", "DOSPUNTOS"));
        resultado.addAll(visit(ctx.tipoPrimitivo()));

        if (ctx.expr() != null) {
            resultado.add(new TextoColoreado(" ", null));
            resultado.add(ColorMapa.colorear("=", "ASIGNAR"));
            resultado.add(new TextoColoreado(" ", null));
            resultado.addAll(visit(ctx.expr()));
        }

        resultado.add(ColorMapa.colorear(";", "PUNTOCOMA"));

        return resultado;
    }

    @Override
    public List<TextoColoreado> visitVariableBooleana(LatinusParser.VariableBooleanaContext ctx) {
        List<TextoColoreado> resultado = new ArrayList<>();

        resultado.add(ColorMapa.colorear("ESTO", "ESTO"));
        resultado.add(new TextoColoreado(" ", null));
        resultado.add(ColorMapa.colorear(ctx.ID().getText(), "ID"));
        resultado.add(ColorMapa.colorear(":", "DOSPUNTOS"));

        if (ctx.VERUM() != null) {
            resultado.add(ColorMapa.colorear("VERUM", "VERUM"));
        } else if (ctx.FALSUS() != null) {
            resultado.add(ColorMapa.colorear("FALSUS", "FALSUS"));
        }

        resultado.add(ColorMapa.colorear(";", "PUNTOCOMA"));

        return resultado;
    }

    @Override
    public List<TextoColoreado> visitTipoPrimitivo(LatinusParser.TipoPrimitivoContext ctx) {
        List<TextoColoreado> resultado = new ArrayList<>();
        String tipo = ctx.getText();
        resultado.add(ColorMapa.colorear(tipo, tipo));
        return resultado;
    }

    @Override
    public List<TextoColoreado> visitTipo(LatinusParser.TipoContext ctx) {
        if (ctx.tipoPrimitivo() != null) {
            return visit(ctx.tipoPrimitivo());
        } else {
            List<TextoColoreado> resultado = new ArrayList<>();
            resultado.add(ColorMapa.colorear(ctx.ID().getText(), "ID"));
            return resultado;
        }
    }

    @Override
    public List<TextoColoreado> visitArregloTipado(LatinusParser.ArregloTipadoContext ctx) {
        List<TextoColoreado> resultado = new ArrayList<>();

        resultado.add(ColorMapa.colorear("SERIES", "SERIES"));
        resultado.add(new TextoColoreado(" ", null));
        resultado.add(ColorMapa.colorear(ctx.ID().getText(), "ID"));
        resultado.add(ColorMapa.colorear("[", "CORCH_A"));
        resultado.add(ColorMapa.colorear(ctx.INT().getText(), "INT"));
        resultado.add(ColorMapa.colorear("]", "CORCH_C"));
        resultado.add(ColorMapa.colorear(":", "DOSPUNTOS"));
        resultado.addAll(visit(ctx.tipo()));

        if (ctx.listaExpr() != null) {
            resultado.add(new TextoColoreado(" ", null));
            resultado.add(ColorMapa.colorear("{", "LLAVE_A"));
            resultado.addAll(visit(ctx.listaExpr()));
            resultado.add(ColorMapa.colorear("}", "LLAVE_C"));
        }

        resultado.add(ColorMapa.colorear(";", "PUNTOCOMA"));

        return resultado;
    }

    @Override
    public List<TextoColoreado> visitArregloBooleano(LatinusParser.ArregloBooleanoContext ctx) {
        List<TextoColoreado> resultado = new ArrayList<>();

        resultado.add(ColorMapa.colorear("SERIES", "SERIES"));
        resultado.add(new TextoColoreado(" ", null));
        resultado.add(ColorMapa.colorear(ctx.ID().getText(), "ID"));
        resultado.add(ColorMapa.colorear("[", "CORCH_A"));
        resultado.add(ColorMapa.colorear(ctx.INT().getText(), "INT"));
        resultado.add(ColorMapa.colorear("]", "CORCH_C"));
        resultado.add(ColorMapa.colorear(":", "DOSPUNTOS"));
        resultado.add(ColorMapa.colorear("{", "LLAVE_A"));
        resultado.addAll(visit(ctx.listaExpr()));
        resultado.add(ColorMapa.colorear("}", "LLAVE_C"));
        resultado.add(ColorMapa.colorear(";", "PUNTOCOMA"));

        return resultado;
    }

    @Override
    public List<TextoColoreado> visitListaExpr(LatinusParser.ListaExprContext ctx) {
        List<TextoColoreado> resultado = new ArrayList<>();

        for (int i = 0; i < ctx.expr().size(); i++) {
            resultado.addAll(visit(ctx.expr(i)));
            if (i < ctx.expr().size() - 1) {
                resultado.add(ColorMapa.colorear(",", "COMA"));
            }
        }

        return resultado;
    }

    @Override
    public List<TextoColoreado> visitStructDef(LatinusParser.StructDefContext ctx) {
        List<TextoColoreado> resultado = new ArrayList<>();

        resultado.add(ColorMapa.colorear("STRUCTURA", "STRUCTURA"));
        resultado.add(new TextoColoreado(" ", null));
        resultado.add(ColorMapa.colorear(ctx.ID().getText(), "ID"));
        resultado.add(ColorMapa.colorear("{", "LLAVE_A"));

        for (LatinusParser.CampoStructContext campo : ctx.campoStruct()) {
            resultado.addAll(visit(campo));
        }

        resultado.add(ColorMapa.colorear("}", "LLAVE_C"));
        resultado.add(ColorMapa.colorear("FINIS", "FINIS"));
        resultado.add(ColorMapa.colorear(";", "PUNTOCOMA"));

        return resultado;
    }

    @Override
    public List<TextoColoreado> visitCampoStruct(LatinusParser.CampoStructContext ctx) {
        List<TextoColoreado> resultado = new ArrayList<>();

        resultado.add(ColorMapa.colorear("ESTO", "ESTO"));
        resultado.add(new TextoColoreado(" ", null));
        resultado.add(ColorMapa.colorear(ctx.ID().getText(), "ID"));
        resultado.add(ColorMapa.colorear(":", "DOSPUNTOS"));
        resultado.addAll(visit(ctx.tipo()));
        resultado.add(ColorMapa.colorear(";", "PUNTOCOMA"));

        return resultado;
    }

    @Override
    public List<TextoColoreado> visitStructInstancia(LatinusParser.StructInstanciaContext ctx) {
        List<TextoColoreado> resultado = new ArrayList<>();

        resultado.add(ColorMapa.colorear("ESTO", "ESTO"));
        resultado.add(new TextoColoreado(" ", null));
        resultado.add(ColorMapa.colorear(ctx.ID(0).getText(), "ID"));
        resultado.add(ColorMapa.colorear(":", "DOSPUNTOS"));
        resultado.add(ColorMapa.colorear(ctx.ID(1).getText(), "ID"));
        resultado.addAll(visit(ctx.literalStruct()));

        return resultado;
    }

    @Override
    public List<TextoColoreado> visitLiteralStruct(LatinusParser.LiteralStructContext ctx) {
        List<TextoColoreado> resultado = new ArrayList<>();

        resultado.add(ColorMapa.colorear("{", "LLAVE_A"));

        for (int i = 0; i < ctx.asignacionCampo().size(); i++) {
            resultado.addAll(visit(ctx.asignacionCampo(i)));
            if (i < ctx.asignacionCampo().size() - 1) {
                resultado.add(ColorMapa.colorear(",", "COMA"));
            }
        }

        resultado.add(ColorMapa.colorear("}", "LLAVE_C"));

        return resultado;
    }

    @Override
    public List<TextoColoreado> visitAsignacionCampo(LatinusParser.AsignacionCampoContext ctx) {
        List<TextoColoreado> resultado = new ArrayList<>();

        resultado.add(ColorMapa.colorear(ctx.ID().getText(), "ID"));
        resultado.add(ColorMapa.colorear(":", "DOSPUNTOS"));
        resultado.addAll(visit(ctx.expr()));

        return resultado;
    }

    @Override
    public List<TextoColoreado> visitFuncionSinRetorno(LatinusParser.FuncionSinRetornoContext ctx) {
        List<TextoColoreado> resultado = new ArrayList<>();

        resultado.add(ColorMapa.colorear("ACTIO", "ACTIO"));
        resultado.add(new TextoColoreado(" ", null));
        resultado.add(ColorMapa.colorear(ctx.ID().getText(), "ID"));
        resultado.add(ColorMapa.colorear("(", "PAR_A"));

        if (ctx.listaParametros() != null) {
            resultado.addAll(visit(ctx.listaParametros()));
        }

        resultado.add(ColorMapa.colorear(")", "PAR_C"));
        resultado.add(ColorMapa.colorear("{", "LLAVE_A"));

        if (ctx.bloqueVariables() != null) {
            resultado.addAll(visit(ctx.bloqueVariables()));
        }

        for (LatinusParser.SentenciaContext sent : ctx.sentencia()) {
            resultado.addAll(visit(sent));
        }

        resultado.add(ColorMapa.colorear("}", "LLAVE_C"));
        resultado.add(ColorMapa.colorear("FINIS", "FINIS"));
        resultado.add(ColorMapa.colorear(";", "PUNTOCOMA"));

        return resultado;
    }

    @Override
    public List<TextoColoreado> visitFuncionConRetorno(LatinusParser.FuncionConRetornoContext ctx) {
        List<TextoColoreado> resultado = new ArrayList<>();

        resultado.add(ColorMapa.colorear("RATIO", "RATIO"));
        resultado.add(new TextoColoreado(" ", null));
        resultado.addAll(visit(ctx.tipo()));
        resultado.add(new TextoColoreado(" ", null));
        resultado.add(ColorMapa.colorear(ctx.ID().getText(), "ID"));
        resultado.add(ColorMapa.colorear("(", "PAR_A"));

        if (ctx.listaParametros() != null) {
            resultado.addAll(visit(ctx.listaParametros()));
        }

        resultado.add(ColorMapa.colorear(")", "PAR_C"));
        resultado.add(ColorMapa.colorear("{", "LLAVE_A"));

        if (ctx.bloqueVariables() != null) {
            resultado.addAll(visit(ctx.bloqueVariables()));
        }

        for (LatinusParser.SentenciaContext sent : ctx.sentencia()) {
            resultado.addAll(visit(sent));
        }

        resultado.add(ColorMapa.colorear("}", "LLAVE_C"));
        resultado.add(ColorMapa.colorear("FINIS", "FINIS"));
        resultado.add(ColorMapa.colorear(";", "PUNTOCOMA"));

        return resultado;
    }

    @Override
    public List<TextoColoreado> visitListaParametros(LatinusParser.ListaParametrosContext ctx) {
        List<TextoColoreado> resultado = new ArrayList<>();

        for (int i = 0; i < ctx.parametro().size(); i++) {
            resultado.addAll(visit(ctx.parametro(i)));
            if (i < ctx.parametro().size() - 1) {
                resultado.add(ColorMapa.colorear(",", "COMA"));
            }
        }

        return resultado;
    }

    @Override
    public List<TextoColoreado> visitParametro(LatinusParser.ParametroContext ctx) {
        List<TextoColoreado> resultado = new ArrayList<>();

        resultado.add(ColorMapa.colorear("ESTO", "ESTO"));
        resultado.add(new TextoColoreado(" ", null));
        resultado.add(ColorMapa.colorear(ctx.ID().getText(), "ID"));
        resultado.add(ColorMapa.colorear(":", "DOSPUNTOS"));
        resultado.addAll(visit(ctx.tipo()));

        return resultado;
    }

    @Override
    public List<TextoColoreado> visitBloqueVariables(LatinusParser.BloqueVariablesContext ctx) {
        List<TextoColoreado> resultado = new ArrayList<>();

        resultado.add(ColorMapa.colorear("VARIABILES", "VARIABILES"));
        resultado.add(ColorMapa.colorear("[", "CORCH_A"));

        for (LatinusParser.DeclaracionVarContext decl : ctx.declaracionVar()) {
            resultado.addAll(visit(decl));
        }

        resultado.add(ColorMapa.colorear("]", "CORCH_C"));

        return resultado;
    }

    @Override
    public List<TextoColoreado> visitSentencia(LatinusParser.SentenciaContext ctx) {
        return visit(ctx.getChild(0));
    }

    @Override
    public List<TextoColoreado> visitIncrementoDecremento(LatinusParser.IncrementoDecrementoContext ctx) {
        List<TextoColoreado> resultado = new ArrayList<>();

        if (ctx.referencia() != null) {
            resultado.addAll(visit(ctx.referencia()));
            if (ctx.INC() != null) {
                resultado.add(ColorMapa.colorear("++", "INC"));
            } else if (ctx.DEC() != null) {
                resultado.add(ColorMapa.colorear("--", "DEC"));
            }
        } else {
            if (ctx.INC() != null) {
                resultado.add(ColorMapa.colorear("++", "INC"));
            } else if (ctx.DEC() != null) {
                resultado.add(ColorMapa.colorear("--", "DEC"));
            }
            resultado.addAll(visit(ctx.referencia()));
        }

        resultado.add(ColorMapa.colorear(";", "PUNTOCOMA"));

        return resultado;
    }

    @Override
    public List<TextoColoreado> visitAsignacion(LatinusParser.AsignacionContext ctx) {
        List<TextoColoreado> resultado = new ArrayList<>();

        resultado.addAll(visit(ctx.referencia()));
        resultado.add(new TextoColoreado(" ", null));
        resultado.add(ColorMapa.colorear("=", "ASIGNAR"));
        resultado.add(new TextoColoreado(" ", null));
        resultado.addAll(visit(ctx.expr()));
        resultado.add(ColorMapa.colorear(";", "PUNTOCOMA"));

        return resultado;
    }

    @Override
    public List<TextoColoreado> visitAsignacionStructLiteral(LatinusParser.AsignacionStructLiteralContext ctx) {
        List<TextoColoreado> resultado = new ArrayList<>();

        resultado.addAll(visit(ctx.referencia()));
        resultado.add(new TextoColoreado(" ", null));
        resultado.add(ColorMapa.colorear("=", "ASIGNAR"));
        resultado.add(new TextoColoreado(" ", null));
        resultado.addAll(visit(ctx.literalStruct()));

        return resultado;
    }

    @Override
    public List<TextoColoreado> visitAccesoAtributo(LatinusParser.AccesoAtributoContext ctx) {
        List<TextoColoreado> resultado = new ArrayList<>();

        resultado.addAll(visit(ctx.referencia()));
        resultado.add(ColorMapa.colorear(".", "PUNTO"));
        resultado.add(ColorMapa.colorear(ctx.ID().getText(), "ID"));

        return resultado;
    }

    @Override
    public List<TextoColoreado> visitAccesoArray(LatinusParser.AccesoArrayContext ctx) {
        List<TextoColoreado> resultado = new ArrayList<>();

        resultado.addAll(visit(ctx.referencia()));
        resultado.add(ColorMapa.colorear("[", "CORCH_A"));
        resultado.addAll(visit(ctx.expr()));
        resultado.add(ColorMapa.colorear("]", "CORCH_C"));

        return resultado;
    }

    @Override
    public List<TextoColoreado> visitReferenciaBase(LatinusParser.ReferenciaBaseContext ctx) {
        List<TextoColoreado> resultado = new ArrayList<>();
        resultado.add(ColorMapa.colorear(ctx.ID().getText(), "ID"));
        return resultado;
    }

    @Override
    public List<TextoColoreado> visitCondicional(LatinusParser.CondicionalContext ctx) {
        List<TextoColoreado> resultado = new ArrayList<>();

        resultado.add(ColorMapa.colorear("SI", "SI"));
        resultado.add(ColorMapa.colorear("(", "PAR_A"));
        resultado.addAll(visit(ctx.expr()));
        resultado.add(ColorMapa.colorear(")", "PAR_C"));
        resultado.addAll(visit(ctx.bloqueSentencias()));

        for (LatinusParser.RamaAliterContext rama : ctx.ramaAliter()) {
            resultado.addAll(visit(rama));
        }

        if (ctx.ramaElse() != null) {
            resultado.addAll(visit(ctx.ramaElse()));
        }

        resultado.add(ColorMapa.colorear("FINIS", "FINIS"));
        resultado.add(ColorMapa.colorear(";", "PUNTOCOMA"));

        return resultado;
    }

    @Override
    public List<TextoColoreado> visitBloqueSentencias(LatinusParser.BloqueSentenciasContext ctx) {
        List<TextoColoreado> resultado = new ArrayList<>();

        resultado.add(ColorMapa.colorear("{", "LLAVE_A"));

        for (LatinusParser.SentenciaContext sent : ctx.sentencia()) {
            resultado.addAll(visit(sent));
        }

        resultado.add(ColorMapa.colorear("}", "LLAVE_C"));

        return resultado;
    }

    @Override
    public List<TextoColoreado> visitRamaAliter(LatinusParser.RamaAliterContext ctx) {
        List<TextoColoreado> resultado = new ArrayList<>();

        resultado.add(ColorMapa.colorear("ALITER", "ALITER"));
        resultado.add(ColorMapa.colorear("(", "PAR_A"));
        resultado.addAll(visit(ctx.expr()));
        resultado.add(ColorMapa.colorear(")", "PAR_C"));
        resultado.addAll(visit(ctx.bloqueSentencias()));

        return resultado;
    }

    @Override
    public List<TextoColoreado> visitRamaElse(LatinusParser.RamaElseContext ctx) {
        List<TextoColoreado> resultado = new ArrayList<>();

        resultado.add(ColorMapa.colorear("ALITER", "ALITER"));
        resultado.addAll(visit(ctx.bloqueSentencias()));

        return resultado;
    }

    @Override
    public List<TextoColoreado> visitCicloDum(LatinusParser.CicloDumContext ctx) {
        List<TextoColoreado> resultado = new ArrayList<>();

        resultado.add(ColorMapa.colorear("DUM", "DUM"));
        resultado.add(ColorMapa.colorear("(", "PAR_A"));
        resultado.addAll(visit(ctx.expr()));
        resultado.add(ColorMapa.colorear(")", "PAR_C"));
        resultado.add(ColorMapa.colorear("{", "LLAVE_A"));

        for (LatinusParser.SentenciaContext sent : ctx.sentencia()) {
            resultado.addAll(visit(sent));
        }

        resultado.add(ColorMapa.colorear("}", "LLAVE_C"));
        resultado.add(ColorMapa.colorear("FINIS", "FINIS"));
        resultado.add(ColorMapa.colorear(";", "PUNTOCOMA"));

        return resultado;
    }

    @Override
    public List<TextoColoreado> visitCicloFacere(LatinusParser.CicloFacereContext ctx) {
        List<TextoColoreado> resultado = new ArrayList<>();

        resultado.add(ColorMapa.colorear("FACERE", "FACERE"));
        resultado.add(ColorMapa.colorear("{", "LLAVE_A"));

        for (LatinusParser.SentenciaContext sent : ctx.sentencia()) {
            resultado.addAll(visit(sent));
        }

        resultado.add(ColorMapa.colorear("}", "LLAVE_C"));
        resultado.add(ColorMapa.colorear("DUM", "DUM"));
        resultado.add(ColorMapa.colorear("(", "PAR_A"));
        resultado.addAll(visit(ctx.expr()));
        resultado.add(ColorMapa.colorear(")", "PAR_C"));
        resultado.add(ColorMapa.colorear(";", "PUNTOCOMA"));

        return resultado;
    }

    @Override
    public List<TextoColoreado> visitCicloPer(LatinusParser.CicloPerContext ctx) {
        List<TextoColoreado> resultado = new ArrayList<>();

        resultado.add(ColorMapa.colorear("PER", "PER"));
        resultado.add(ColorMapa.colorear("(", "PAR_A"));
        resultado.addAll(visit(ctx.variable()));
        resultado.add(ColorMapa.colorear(";", "PUNTOCOMA"));
        resultado.addAll(visit(ctx.expr()));
        resultado.add(ColorMapa.colorear(";", "PUNTOCOMA"));
        resultado.addAll(visit(ctx.incremento()));
        resultado.add(ColorMapa.colorear(")", "PAR_C"));
        resultado.add(ColorMapa.colorear("{", "LLAVE_A"));

        for (LatinusParser.SentenciaContext sent : ctx.sentencia()) {
            resultado.addAll(visit(sent));
        }

        resultado.add(ColorMapa.colorear("}", "LLAVE_C"));

        return resultado;
    }

    @Override
    public List<TextoColoreado> visitIncremento(LatinusParser.IncrementoContext ctx) {
        List<TextoColoreado> resultado = new ArrayList<>();

        if (ctx.referencia() != null) {
            resultado.addAll(visit(ctx.referencia()));
            if (ctx.INC() != null) {
                resultado.add(ColorMapa.colorear("++", "INC"));
            } else if (ctx.DEC() != null) {
                resultado.add(ColorMapa.colorear("--", "DEC"));
            } else if (ctx.ASIGNAR() != null) {
                resultado.add(new TextoColoreado(" ", null));
                resultado.add(ColorMapa.colorear("=", "ASIGNAR"));
                resultado.add(new TextoColoreado(" ", null));
                resultado.addAll(visit(ctx.expr()));
            }
        }

        return resultado;
    }

    @Override
    public List<TextoColoreado> visitInterrupcionCiclo(LatinusParser.InterrupcionCicloContext ctx) {
        List<TextoColoreado> resultado = new ArrayList<>();

        if (ctx.PERGE() != null) {
            resultado.add(ColorMapa.colorear("PERGE", "PERGE"));
        } else if (ctx.INTERRUMPE() != null) {
            resultado.add(ColorMapa.colorear("INTERRUMPE", "INTERRUMPE"));
        }

        resultado.add(ColorMapa.colorear(";", "PUNTOCOMA"));

        return resultado;
    }

    @Override
    public List<TextoColoreado> visitRetorno(LatinusParser.RetornoContext ctx) {
        List<TextoColoreado> resultado = new ArrayList<>();

        resultado.add(ColorMapa.colorear("REDDERE", "REDDERE"));
        resultado.add(new TextoColoreado(" ", null));
        resultado.addAll(visit(ctx.expr()));
        resultado.add(ColorMapa.colorear(";", "PUNTOCOMA"));

        return resultado;
    }

    @Override
    public List<TextoColoreado> visitLectura(LatinusParser.LecturaContext ctx) {
        List<TextoColoreado> resultado = new ArrayList<>();

        if (ctx.ID() != null) {
            resultado.add(ColorMapa.colorear(ctx.ID().getText(), "ID"));
            resultado.add(new TextoColoreado(" ", null));
        }

        resultado.add(ColorMapa.colorear("<<", "LEER"));

        return resultado;
    }

    @Override
    public List<TextoColoreado> visitEscritura(LatinusParser.EscrituraContext ctx) {
        List<TextoColoreado> resultado = new ArrayList<>();

        for (int i = 0; i < ctx.expr().size(); i++) {
            resultado.add(ColorMapa.colorear(">>", "ESCRIBIR"));
            resultado.add(new TextoColoreado(" ", null));
            resultado.addAll(visit(ctx.expr(i)));
            if (i < ctx.expr().size() - 1) {
                resultado.add(new TextoColoreado(" ", null));
            }
        }

        resultado.add(ColorMapa.colorear(";", "PUNTOCOMA"));

        return resultado;
    }

    @Override
    public List<TextoColoreado> visitLlamadaFuncion(LatinusParser.LlamadaFuncionContext ctx) {
        List<TextoColoreado> resultado = new ArrayList<>();

        resultado.add(ColorMapa.colorear(ctx.ID().getText(), "ID"));
        resultado.add(ColorMapa.colorear("(", "PAR_A"));

        if (ctx.listaArgumentos() != null) {
            resultado.addAll(visit(ctx.listaArgumentos()));
        }

        resultado.add(ColorMapa.colorear(")", "PAR_C"));

        return resultado;
    }

    @Override
    public List<TextoColoreado> visitListaArgumentos(LatinusParser.ListaArgumentosContext ctx) {
        List<TextoColoreado> resultado = new ArrayList<>();

        for (int i = 0; i < ctx.expr().size(); i++) {
            resultado.addAll(visit(ctx.expr(i)));
            if (i < ctx.expr().size() - 1) {
                resultado.add(ColorMapa.colorear(",", "COMA"));
            }
        }

        return resultado;
    }

    @Override
    public List<TextoColoreado> visitExprFalsus(LatinusParser.ExprFalsusContext ctx) {
        List<TextoColoreado> resultado = new ArrayList<>();
        resultado.add(ColorMapa.colorear("FALSUS", "FALSUS"));
        return resultado;
    }

    @Override
    public List<TextoColoreado> visitExprIncDecPostfijo(LatinusParser.ExprIncDecPostfijoContext ctx) {
        List<TextoColoreado> resultado = new ArrayList<>();
        resultado.add(ColorMapa.colorear(ctx.ID().getText(), "ID"));
        if (ctx.INC() != null) {
            resultado.add(ColorMapa.colorear("++", "INC"));
        } else if (ctx.DEC() != null) {
            resultado.add(ColorMapa.colorear("--", "DEC"));
        }
        return resultado;
    }

    @Override
    public List<TextoColoreado> visitExprRelacional(LatinusParser.ExprRelacionalContext ctx) {
        List<TextoColoreado> resultado = new ArrayList<>();
        resultado.addAll(visit(ctx.expr(0)));
        resultado.add(new TextoColoreado(" ", null));
        resultado.add(ColorMapa.colorear(ctx.op.getText(), ctx.op.getText()));
        resultado.add(new TextoColoreado(" ", null));
        resultado.addAll(visit(ctx.expr(1)));
        return resultado;
    }

    @Override
    public List<TextoColoreado> visitExprParentesis(LatinusParser.ExprParentesisContext ctx) {
        List<TextoColoreado> resultado = new ArrayList<>();
        resultado.add(ColorMapa.colorear("(", "PAR_A"));
        resultado.addAll(visit(ctx.expr()));
        resultado.add(ColorMapa.colorear(")", "PAR_C"));
        return resultado;
    }

    @Override
    public List<TextoColoreado> visitExprIncDecPrefijo(LatinusParser.ExprIncDecPrefijoContext ctx) {
        List<TextoColoreado> resultado = new ArrayList<>();
        if (ctx.INC() != null) {
            resultado.add(ColorMapa.colorear("++", "INC"));
        } else if (ctx.DEC() != null) {
            resultado.add(ColorMapa.colorear("--", "DEC"));
        }
        resultado.add(ColorMapa.colorear(ctx.ID().getText(), "ID"));
        return resultado;
    }

    @Override
    public List<TextoColoreado> visitExprLlamada(LatinusParser.ExprLlamadaContext ctx) {
        return visit(ctx.llamadaFuncion());
    }

    @Override
    public List<TextoColoreado> visitExprSumaResta(LatinusParser.ExprSumaRestaContext ctx) {
        List<TextoColoreado> resultado = new ArrayList<>();
        resultado.addAll(visit(ctx.expr(0)));
        resultado.add(new TextoColoreado(" ", null));
        resultado.add(ColorMapa.colorear(ctx.op.getText(), ctx.op.getText()));
        resultado.add(new TextoColoreado(" ", null));
        resultado.addAll(visit(ctx.expr(1)));
        return resultado;
    }

    @Override
    public List<TextoColoreado> visitExprOr(LatinusParser.ExprOrContext ctx) {
        List<TextoColoreado> resultado = new ArrayList<>();
        resultado.addAll(visit(ctx.expr(0)));
        resultado.add(new TextoColoreado(" ", null));
        resultado.add(ColorMapa.colorear("||", "OR"));
        resultado.add(new TextoColoreado(" ", null));
        resultado.addAll(visit(ctx.expr(1)));
        return resultado;
    }


    @Override
    public List<TextoColoreado> visitExprTexto(LatinusParser.ExprTextoContext ctx) {
        List<TextoColoreado> resultado = new ArrayList<>();
        resultado.add(ColorMapa.colorear(ctx.STRING().getText(), "STRING"));
        return resultado;
    }


    @Override
    public List<TextoColoreado> visitExprNegacion(LatinusParser.ExprNegacionContext ctx) {

        List<TextoColoreado> resultado = new ArrayList<>();
        resultado.add(ColorMapa.colorear("NON", "NON"));
        resultado.add(new TextoColoreado(" ", null));
        resultado.addAll(visit(ctx.expr()));
        return resultado;
    }


    @Override
    public List<TextoColoreado> visitExprEntero(LatinusParser.ExprEnteroContext ctx) {
        List<TextoColoreado> resultado = new ArrayList<>();
        resultado.add(ColorMapa.colorear(ctx.INT().getText(), "INT"));
        return resultado;
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public List<TextoColoreado> visitExprIgualdad(LatinusParser.ExprIgualdadContext ctx) {
            List<TextoColoreado> resultado = new ArrayList<>();
            resultado.addAll(visit(ctx.expr(0)));
            resultado.add(new TextoColoreado(" ", null));
            resultado.add(ColorMapa.colorear(ctx.op.getText(), ctx.op.getText()));
            resultado.add(new TextoColoreado(" ", null));
            resultado.addAll(visit(ctx.expr(1)));
            return resultado;
    }

    @Override
    public List<TextoColoreado> visitExprAnd(LatinusParser.ExprAndContext ctx) {
        List<TextoColoreado> resultado = new ArrayList<>();
        resultado.addAll(visit(ctx.expr(0)));
        resultado.add(new TextoColoreado("", null));
        resultado.add(ColorMapa.colorear("&&", "AND"));
        resultado.add(ColorMapa.colorear("", null));
        resultado.addAll(visit(ctx.expr(1)));
        return resultado;
    }

    @Override
    public List<TextoColoreado> visitExprVerum(LatinusParser.ExprVerumContext ctx) {
        List<TextoColoreado> resultado = new ArrayList<>();
        resultado.add(ColorMapa.colorear("VERUM", "VERUM"));
        return resultado;
    }

    @Override
    public List<TextoColoreado> visitExprReferencia(LatinusParser.ExprReferenciaContext ctx) {
        return visit(ctx.referencia());
    }

    @Override
    public List<TextoColoreado> visitExprMulDiv(LatinusParser.ExprMulDivContext ctx) {
        List<TextoColoreado> resultado = new ArrayList<>();
        resultado.addAll(visit(ctx.expr(0)));
        resultado.add(new TextoColoreado("", null));
        resultado.add(ColorMapa.colorear(ctx.op.getText(), ctx.op.getText()));
        resultado.add(new TextoColoreado("", null));
        resultado.addAll(visit(ctx.expr(1)));
        return resultado;
    }

    @Override
    public List<TextoColoreado> visitExprCaracter(LatinusParser.ExprCaracterContext ctx) {
        List<TextoColoreado> resultado = new ArrayList<>();
        resultado.add(ColorMapa.colorear(ctx.CHAR().getText(), "CAHR"));
        return resultado;
    }

    @Override
    public List<TextoColoreado> visitExprDecimal(LatinusParser.ExprDecimalContext ctx) {
        List<TextoColoreado> resultado = new ArrayList<>();
        resultado.add(ColorMapa.colorear(ctx.FLOAT().getText(), "FLOAT"));
        return resultado;
    }
}
