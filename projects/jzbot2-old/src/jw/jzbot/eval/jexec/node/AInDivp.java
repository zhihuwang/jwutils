/* This file was generated by SableCC (http://www.sablecc.org/). */

package jw.jzbot.eval.jexec.node;

import jw.jzbot.eval.jexec.analysis.*;

@SuppressWarnings("nls")
public final class AInDivp extends PDivp
{
    private PDivp _first_;
    private TDiv _div_;
    private PUnmp _second_;

    public AInDivp()
    {
        // Constructor
    }

    public AInDivp(
        @SuppressWarnings("hiding") PDivp _first_,
        @SuppressWarnings("hiding") TDiv _div_,
        @SuppressWarnings("hiding") PUnmp _second_)
    {
        // Constructor
        setFirst(_first_);

        setDiv(_div_);

        setSecond(_second_);

    }

    @Override
    public Object clone()
    {
        return new AInDivp(
            cloneNode(this._first_),
            cloneNode(this._div_),
            cloneNode(this._second_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAInDivp(this);
    }

    public PDivp getFirst()
    {
        return this._first_;
    }

    public void setFirst(PDivp node)
    {
        if(this._first_ != null)
        {
            this._first_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._first_ = node;
    }

    public TDiv getDiv()
    {
        return this._div_;
    }

    public void setDiv(TDiv node)
    {
        if(this._div_ != null)
        {
            this._div_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._div_ = node;
    }

    public PUnmp getSecond()
    {
        return this._second_;
    }

    public void setSecond(PUnmp node)
    {
        if(this._second_ != null)
        {
            this._second_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._second_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._first_)
            + toString(this._div_)
            + toString(this._second_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._first_ == child)
        {
            this._first_ = null;
            return;
        }

        if(this._div_ == child)
        {
            this._div_ = null;
            return;
        }

        if(this._second_ == child)
        {
            this._second_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._first_ == oldChild)
        {
            setFirst((PDivp) newChild);
            return;
        }

        if(this._div_ == oldChild)
        {
            setDiv((TDiv) newChild);
            return;
        }

        if(this._second_ == oldChild)
        {
            setSecond((PUnmp) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
