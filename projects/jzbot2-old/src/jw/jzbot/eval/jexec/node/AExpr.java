/* This file was generated by SableCC (http://www.sablecc.org/). */

package jw.jzbot.eval.jexec.node;

import jw.jzbot.eval.jexec.analysis.*;

@SuppressWarnings("nls")
public final class AExpr extends PExpr
{
    private PAddp _addp_;

    public AExpr()
    {
        // Constructor
    }

    public AExpr(
        @SuppressWarnings("hiding") PAddp _addp_)
    {
        // Constructor
        setAddp(_addp_);

    }

    @Override
    public Object clone()
    {
        return new AExpr(
            cloneNode(this._addp_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAExpr(this);
    }

    public PAddp getAddp()
    {
        return this._addp_;
    }

    public void setAddp(PAddp node)
    {
        if(this._addp_ != null)
        {
            this._addp_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._addp_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._addp_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._addp_ == child)
        {
            this._addp_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._addp_ == oldChild)
        {
            setAddp((PAddp) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}