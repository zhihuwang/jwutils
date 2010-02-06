/* This file was generated by SableCC (http://www.sablecc.org/). */

package expression.node;

import expression.analysis.*;

@SuppressWarnings("nls")
public final class ANextDivp extends PDivp
{
    private PUnmp _next_;

    public ANextDivp()
    {
        // Constructor
    }

    public ANextDivp(
        @SuppressWarnings("hiding") PUnmp _next_)
    {
        // Constructor
        setNext(_next_);

    }

    @Override
    public Object clone()
    {
        return new ANextDivp(
            cloneNode(this._next_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseANextDivp(this);
    }

    public PUnmp getNext()
    {
        return this._next_;
    }

    public void setNext(PUnmp node)
    {
        if(this._next_ != null)
        {
            this._next_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._next_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._next_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._next_ == child)
        {
            this._next_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._next_ == oldChild)
        {
            setNext((PUnmp) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}