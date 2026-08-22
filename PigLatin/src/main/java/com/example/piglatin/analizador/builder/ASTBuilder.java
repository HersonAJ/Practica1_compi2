package com.example.piglatin.analizador.builder;

import com.example.piglatin.analizador.ast.*;
import com.example.piglatin.analizador.gramatica.LatinusParser;
import com.example.piglatin.analizador.gramatica.LatinusParserBaseVisitor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ASTBuilder extends LatinusParserBaseVisitor<NodoAST> {

    // ===================== Programa =====================

    @Override
    public NodoAST visitPrograma(LatinusParser.ProgramaContext ctx) {
        List<NodoSentencia> globales = new ArrayList<>();
        if (ctx.seccionVariables() != null) {
            for (LatinusParser.DeclaracionVarContext d : ctx.seccionVariables().declaracionVar()) {
                NodoAST nodo = visit(d);
                if (nodo instanceof NodoSentencia) {
                    globales.add((NodoSentencia) nodo);
                }
            }
        }

        List<NodoFuncion> funciones = new ArrayList<>();
        if (ctx.seccionFunciones() != null) {
            for (LatinusParser.FuncionContext f : ctx.seccionFunciones().funcion()) {
                NodoAST nodo = visit(f);
                if (nodo instanceof NodoFuncion) {
                    funciones.add((NodoFuncion) nodo);
                }
            }
        }

        return new NodoPrograma(globales, funciones, construirCuerpo(ctx.seccionMain().sentencia()));
    }

    // ===================== Declaraciones =====================

    @Override
    public NodoAST visitDeclaracionVar(LatinusParser.DeclaracionVarContext ctx) {
        if (ctx.getChild(0) == null) {
            return null;
        }
        return visit(ctx.getChild(0));
    }

    @Override
    public NodoAST visitVariablePrimitiva(LatinusParser.VariablePrimitivaContext ctx) {
        if (ctx.ID() == null || ctx.tipoPrimitivo() == null) {
            return null;
        }
        String tipo = ctx.tipoPrimitivo().getText();
        NodoExpr valor = ctx.expr() != null ? (NodoExpr) visit(ctx.expr()) : null;
        return new NodoSentencia.DeclaracionVariable(linea(ctx), ctx.ID().getText(), tipo, valor);
    }
    @Override
    public NodoAST visitVariableBooleana(LatinusParser.VariableBooleanaContext ctx) {
        boolean valor = ctx.VERUM() != null;
        NodoExpr literal = new NodoExpr.LiteralBooleano(linea(ctx), valor);
        return new NodoSentencia.DeclaracionVariable(linea(ctx), ctx.ID().getText(), "booleano", literal);
    }

    @Override
    public NodoAST visitArregloTipado(LatinusParser.ArregloTipadoContext ctx) {
        if (ctx.ID() == null || ctx.INT() == null || ctx.tipo() == null) {
            return null;
        }
        List<NodoExpr> valores = new ArrayList<>();
        if (ctx.listaExpr() != null) {
            for (LatinusParser.ExprContext e : ctx.listaExpr().expr()) {
                NodoExpr expr = (NodoExpr) visit(e);
                if (expr != null) {
                    valores.add(expr);
                }
            }
        }
        try {
            return new NodoSentencia.DeclaracionArreglo(
                    linea(ctx), ctx.ID().getText(), Integer.parseInt(ctx.INT().getText()),
                    ctx.tipo().getText(), valores);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public NodoAST visitArregloBooleano(LatinusParser.ArregloBooleanoContext ctx) {
        if (ctx.ID() == null || ctx.INT() == null || ctx.listaExpr() == null) {
            return null;
        }
        List<NodoExpr> valores = new ArrayList<>();
        for (LatinusParser.ExprContext e : ctx.listaExpr().expr()) {
            NodoExpr expr = (NodoExpr) visit(e);
            if (expr != null) {
                valores.add(expr);
            }
        }
        try {
            return new NodoSentencia.DeclaracionArreglo(
                    linea(ctx), ctx.ID().getText(), Integer.parseInt(ctx.INT().getText()),
                    "booleano", valores);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public NodoAST visitStructDef(LatinusParser.StructDefContext ctx) {
        if (ctx.ID() == null) {
            return null;
        }
        List<NodoSentencia.CampoStruct> campos = new ArrayList<>();
        for (LatinusParser.CampoStructContext c : ctx.campoStruct()) {
            if (c.ID() != null && c.tipo() != null) {
                campos.add(new NodoSentencia.CampoStruct(c.ID().getText(), c.tipo().getText()));
            }
        }
        return new NodoSentencia.DefinicionStruct(linea(ctx), ctx.ID().getText(), campos);
    }

    @Override
    public NodoAST visitStructInstancia(LatinusParser.StructInstanciaContext ctx) {
        if (ctx.ID(0) == null || ctx.ID(1) == null || ctx.literalStruct() == null) {
            return null;
        }
        return new NodoSentencia.InstanciaStruct(
                linea(ctx),
                ctx.ID(0).getText(),
                ctx.ID(1).getText(),
                construirMapaCampos(ctx.literalStruct()));
    }

    private Map<String, NodoExpr> construirMapaCampos(LatinusParser.LiteralStructContext ctx) {
        Map<String, NodoExpr> valores = new LinkedHashMap<>();
        if (ctx == null) {
            return valores;
        }
        for (LatinusParser.AsignacionCampoContext c : ctx.asignacionCampo()) {
            if (c.ID() != null && c.expr() != null) {
                NodoExpr expr = (NodoExpr) visit(c.expr());
                if (expr != null) {
                    valores.put(c.ID().getText(), expr);
                }
            }
        }
        return valores;
    }

    // ===================== Funciones =====================

    @Override
    public NodoAST visitFuncionSinRetorno(LatinusParser.FuncionSinRetornoContext ctx) {
        return construirFuncion(linea(ctx), ctx.ID().getText(), null,
                ctx.listaParametros(), ctx.bloqueVariables(), ctx.sentencia());
    }

    @Override
    public NodoAST visitFuncionConRetorno(LatinusParser.FuncionConRetornoContext ctx) {
        return construirFuncion(linea(ctx), ctx.ID().getText(), ctx.tipo().getText(),
                ctx.listaParametros(), ctx.bloqueVariables(), ctx.sentencia());
    }

    private NodoFuncion construirFuncion(int linea, String nombre, String tipoRetorno,
                                         LatinusParser.ListaParametrosContext parametrosCtx,
                                         LatinusParser.BloqueVariablesContext varsCtx,
                                         List<LatinusParser.SentenciaContext> sentenciasCtx) {

        List<NodoFuncion.Parametro> parametros = new ArrayList<>();
        if (parametrosCtx != null) {
            for (LatinusParser.ParametroContext p : parametrosCtx.parametro()) {
                parametros.add(new NodoFuncion.Parametro(p.ID().getText(), p.tipo().getText()));
            }
        }

        List<NodoSentencia> variablesLocales = new ArrayList<>();
        if (varsCtx != null) {
            for (LatinusParser.DeclaracionVarContext d : varsCtx.declaracionVar()) {
                variablesLocales.add((NodoSentencia) visit(d));
            }
        }

        return new NodoFuncion(linea, nombre, parametros, tipoRetorno,
                variablesLocales, construirCuerpo(sentenciasCtx));
    }

    // ===================== Sentencias =====================

    @Override
    public NodoAST visitSentencia(LatinusParser.SentenciaContext ctx) {
        if (ctx == null) {
            return null;
        }
        if (ctx.llamadaFuncion() != null) {
            NodoExpr.LlamadaFuncion llamada = (NodoExpr.LlamadaFuncion) visit(ctx.llamadaFuncion());
            if (llamada != null) {
                return new NodoSentencia.LlamadaFuncionSentencia(linea(ctx), llamada);
            }
            return null;
        }
        if (ctx.getChild(0) == null) {
            return null;
        }
        return visit(ctx.getChild(0));
    }

    private List<NodoSentencia> construirCuerpo(List<LatinusParser.SentenciaContext> sentencias) {
        List<NodoSentencia> cuerpo = new ArrayList<>();
        for (LatinusParser.SentenciaContext s : sentencias) {
            NodoAST nodo = visit(s);
            if (nodo instanceof NodoSentencia) {
                cuerpo.add((NodoSentencia) nodo);
            }
        }
        return cuerpo;
    }

    @Override
    public NodoAST visitAsignacion(LatinusParser.AsignacionContext ctx) {
        NodoExpr referencia = (NodoExpr) visit(ctx.referencia());
        NodoExpr valor = (NodoExpr) visit(ctx.expr());
        return new NodoSentencia.Asignacion(linea(ctx), referencia, valor);
    }

    @Override
    public NodoAST visitAsignacionStructLiteral(LatinusParser.AsignacionStructLiteralContext ctx) {
        NodoExpr referencia = (NodoExpr) visit(ctx.referencia());
        NodoExpr.LiteralStruct valor =
                new NodoExpr.LiteralStruct(linea(ctx), construirMapaCampos(ctx.literalStruct()));
        return new NodoSentencia.AsignacionStructLiteral(linea(ctx), referencia, valor);
    }

    @Override
    public NodoAST visitReferenciaBase(LatinusParser.ReferenciaBaseContext ctx) {
        return new NodoExpr.Identificador(linea(ctx), ctx.ID().getText());
    }

    @Override
    public NodoAST visitAccesoAtributo(LatinusParser.AccesoAtributoContext ctx) {
        NodoExpr base = (NodoExpr) visit(ctx.referencia());
        return new NodoExpr.AccesoAtributo(linea(ctx), base, ctx.ID().getText());
    }

    @Override
    public NodoAST visitAccesoArray(LatinusParser.AccesoArrayContext ctx) {
        NodoExpr base = (NodoExpr) visit(ctx.referencia());
        return new NodoExpr.AccesoArray(linea(ctx), base, (NodoExpr) visit(ctx.expr()));
    }

    @Override
    public NodoAST visitCondicional(LatinusParser.CondicionalContext ctx) {
        if (ctx.expr() == null || ctx.bloqueSentencias() == null) {
            return null;
        }
        List<NodoSentencia.Rama> ramas = new ArrayList<>();

        NodoExpr condicionIf = (NodoExpr) visit(ctx.expr());
        if (condicionIf != null) {
            ramas.add(new NodoSentencia.Rama(
                    condicionIf,
                    construirCuerpo(ctx.bloqueSentencias().sentencia())));
        }

        for (LatinusParser.RamaAliterContext r : ctx.ramaAliter()) {
            if (r.expr() != null && r.bloqueSentencias() != null) {
                NodoExpr condicion = (NodoExpr) visit(r.expr());
                if (condicion != null) {
                    ramas.add(new NodoSentencia.Rama(
                            condicion,
                            construirCuerpo(r.bloqueSentencias().sentencia())));
                }
            }
        }

        List<NodoSentencia> elseCuerpo = ctx.ramaElse() != null
                ? construirCuerpo(ctx.ramaElse().bloqueSentencias().sentencia())
                : null;

        return new NodoSentencia.Condicional(linea(ctx), ramas, elseCuerpo);
    }

    @Override
    public NodoAST visitCicloDum(LatinusParser.CicloDumContext ctx) {
        return new NodoSentencia.CicloDum(
                linea(ctx), (NodoExpr) visit(ctx.expr()), construirCuerpo(ctx.sentencia()));
    }

    @Override
    public NodoAST visitCicloFacere(LatinusParser.CicloFacereContext ctx) {
        return new NodoSentencia.CicloFacere(
                linea(ctx), construirCuerpo(ctx.sentencia()), (NodoExpr) visit(ctx.expr()));
    }

    @Override
    public NodoAST visitCicloPer(LatinusParser.CicloPerContext ctx) {
        if (ctx.variable() == null || ctx.expr() == null || ctx.incremento() == null) {
            return null;
        }
        NodoSentencia.DeclaracionVariable inicializacion =
                (NodoSentencia.DeclaracionVariable) visit(ctx.variable());
        NodoExpr condicion = (NodoExpr) visit(ctx.expr());
        NodoSentencia incremento = construirIncremento(ctx.incremento());
        if (inicializacion == null || condicion == null || incremento == null) {
            return null;
        }
        return new NodoSentencia.CicloPer(
                linea(ctx), inicializacion, condicion, incremento, construirCuerpo(ctx.sentencia()));
    }

    // incremento: referencia (INC | DEC) | referencia ASIGNAR expr
    private NodoSentencia construirIncremento(LatinusParser.IncrementoContext ctx) {
        if (ctx == null || ctx.referencia() == null) {
            return null;
        }
        int linea = linea(ctx);
        if (ctx.INC() != null || ctx.DEC() != null) {
            NodoExpr referencia = (NodoExpr) visit(ctx.referencia());
            if (referencia == null) {
                return null;
            }
            String operador = ctx.INC() != null ? "+" : "-";
            NodoExpr uno = new NodoExpr.LiteralEntero(linea, 1);
            NodoExpr suma = new NodoExpr.Binaria(linea, operador, referencia, uno);
            return new NodoSentencia.Asignacion(linea, referencia, suma);
        }
        NodoExpr referencia = (NodoExpr) visit(ctx.referencia());
        NodoExpr valor = (NodoExpr) visit(ctx.expr());
        if (referencia == null || valor == null) {
            return null;
        }
        return new NodoSentencia.Asignacion(linea, referencia, valor);
    }

    @Override
    public NodoAST visitRetorno(LatinusParser.RetornoContext ctx) {
        return new NodoSentencia.Retorno(linea(ctx), (NodoExpr) visit(ctx.expr()));
    }

    @Override
    public NodoAST visitLectura(LatinusParser.LecturaContext ctx) {
        String variable = ctx.ID() != null ? ctx.ID().getText() : null;
        return new NodoSentencia.Lectura(linea(ctx), variable);
    }

    @Override
    public NodoAST visitEscritura(LatinusParser.EscrituraContext ctx) {
        List<NodoExpr> valores = new ArrayList<>();
        for (LatinusParser.ExprContext e : ctx.expr()) {
            valores.add((NodoExpr) visit(e));
        }
        return new NodoSentencia.Escritura(linea(ctx), valores);
    }

    @Override
    public NodoAST visitInterrupcionCiclo(LatinusParser.InterrupcionCicloContext ctx) {
        String tipo = ctx.PERGE() != null ? "perge" : "interrumpe";
        return new NodoSentencia.InterrupcionCiclo(linea(ctx), tipo);
    }

    // ===================== Expresiones =====================

    @Override
    public NodoAST visitExprParentesis(LatinusParser.ExprParentesisContext ctx) {
        return visit(ctx.expr());
    }

    @Override
    public NodoAST visitExprIncDecPrefijo(LatinusParser.ExprIncDecPrefijoContext ctx) {
        String operador = ctx.INC() != null ? "++" : "--";
        NodoExpr id = new NodoExpr.Identificador(linea(ctx), ctx.ID().getText());
        return new NodoExpr.Unaria(linea(ctx), operador, id, true);
    }

    @Override
    public NodoAST visitExprIncDecPostfijo(LatinusParser.ExprIncDecPostfijoContext ctx) {
        String operador = ctx.INC() != null ? "++" : "--";
        NodoExpr id = new NodoExpr.Identificador(linea(ctx), ctx.ID().getText());
        return new NodoExpr.Unaria(linea(ctx), operador, id, false);
    }

    @Override
    public NodoAST visitExprNegacion(LatinusParser.ExprNegacionContext ctx) {
        return new NodoExpr.Unaria(linea(ctx), "non", (NodoExpr) visit(ctx.expr()), true);
    }

    @Override
    public NodoAST visitExprMulDiv(LatinusParser.ExprMulDivContext ctx) {
        return construirBinaria(linea(ctx), ctx.op.getText(), ctx.expr(0), ctx.expr(1));
    }

    @Override
    public NodoAST visitExprSumaResta(LatinusParser.ExprSumaRestaContext ctx) {
        return construirBinaria(linea(ctx), ctx.op.getText(), ctx.expr(0), ctx.expr(1));
    }

    @Override
    public NodoAST visitExprRelacional(LatinusParser.ExprRelacionalContext ctx) {
        return construirBinaria(linea(ctx), ctx.op.getText(), ctx.expr(0), ctx.expr(1));
    }

    @Override
    public NodoAST visitExprIgualdad(LatinusParser.ExprIgualdadContext ctx) {
        return construirBinaria(linea(ctx), ctx.op.getText(), ctx.expr(0), ctx.expr(1));
    }

    @Override
    public NodoAST visitExprAnd(LatinusParser.ExprAndContext ctx) {
        return construirBinaria(linea(ctx), "&&", ctx.expr(0), ctx.expr(1));
    }

    @Override
    public NodoAST visitExprOr(LatinusParser.ExprOrContext ctx) {
        return construirBinaria(linea(ctx), "||", ctx.expr(0), ctx.expr(1));
    }

    private NodoExpr construirBinaria(int linea, String operador,
                                      LatinusParser.ExprContext izqCtx, LatinusParser.ExprContext derCtx) {
        if (izqCtx == null || derCtx == null) {
            return null;
        }
        NodoExpr izq = (NodoExpr) visit(izqCtx);
        NodoExpr der = (NodoExpr) visit(derCtx);
        if (izq == null || der == null) {
            return null;
        }
        return new NodoExpr.Binaria(linea, operador, izq, der);
    }

    @Override
    public NodoAST visitExprLlamada(LatinusParser.ExprLlamadaContext ctx) {
        return visit(ctx.llamadaFuncion());
    }

    @Override
    public NodoAST visitLlamadaFuncion(LatinusParser.LlamadaFuncionContext ctx) {
        if (ctx.ID() == null) {
            return null;
        }
        List<NodoExpr> argumentos = new ArrayList<>();
        if (ctx.listaArgumentos() != null) {
            for (LatinusParser.ExprContext e : ctx.listaArgumentos().expr()) {
                NodoExpr arg = (NodoExpr) visit(e);
                if (arg != null) {
                    argumentos.add(arg);
                }
            }
        }
        return new NodoExpr.LlamadaFuncion(linea(ctx), ctx.ID().getText(), argumentos);
    }

    @Override
    public NodoAST visitExprReferencia(LatinusParser.ExprReferenciaContext ctx) {
        return visit(ctx.referencia());
    }

    @Override
    public NodoAST visitExprEntero(LatinusParser.ExprEnteroContext ctx) {
        return new NodoExpr.LiteralEntero(linea(ctx), Integer.parseInt(ctx.getText()));
    }

    @Override
    public NodoAST visitExprDecimal(LatinusParser.ExprDecimalContext ctx) {
        return new NodoExpr.LiteralDecimal(linea(ctx), Double.parseDouble(ctx.getText()));
    }

    @Override
    public NodoAST visitExprTexto(LatinusParser.ExprTextoContext ctx) {
        String texto = ctx.getText();
        return new NodoExpr.LiteralTexto(linea(ctx), texto.substring(1, texto.length() - 1));
    }

    @Override
    public NodoAST visitExprCaracter(LatinusParser.ExprCaracterContext ctx) {
        String texto = ctx.getText();
        return new NodoExpr.LiteralCaracter(linea(ctx), texto.charAt(1));
    }

    @Override
    public NodoAST visitExprVerum(LatinusParser.ExprVerumContext ctx) {
        return new NodoExpr.LiteralBooleano(linea(ctx), true);
    }

    @Override
    public NodoAST visitExprFalsus(LatinusParser.ExprFalsusContext ctx) {
        return new NodoExpr.LiteralBooleano(linea(ctx), false);
    }

    // ===================== Utilidad =====================

    private int linea(org.antlr.v4.runtime.ParserRuleContext ctx) {
        return ctx.getStart().getLine();
    }
}