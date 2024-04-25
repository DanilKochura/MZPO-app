package lk.mzpo.ru.models

import CoursePreview

class CourseFilterModel(var fromCost: Int?, var toCost: Int?, var dist: Boolean, var sale: Boolean)
{
    public fun filterBase(predicate: CoursePreview): Boolean {
        if(sale && !dist)
        {
            if(predicate.prices.sale15 != null && predicate.prices.sale15 != 0)
            {
                if(fromCost?.until(toCost!!)?.contains(predicate.prices.sale15) == true)
                {
                    return true;
                }
            }
            if(predicate.prices.weekend != null && predicate.prices.weekend != 0)
            {
                if(fromCost?.until(toCost!!)?.contains(predicate.prices.weekend) == true)
                {
                    return true;
                }
            }
            if(predicate.prices.ind != null && predicate.prices.ind != 0)
            {
                if(fromCost?.until(toCost!!)?.contains(predicate.prices.ind) == true)
                {
                    return true;
                }
            }
            return false
        } else if (!sale && dist)
        {
            if(predicate.prices.dist != null && predicate.prices.dist != 0)
            {
                if(fromCost?.until(toCost!!)?.contains(predicate.prices.dist) == true)
                {
                    return true;
                }
            }
            return false
        }
        else
        {
            if(predicate.prices.dist != null && predicate.prices.dist != 0)
            {
                if(fromCost?.until(toCost!!)?.contains(predicate.prices.dist) == true)
                {
                    return true;
                }
            }
            if(predicate.prices.sale15 != null && predicate.prices.sale15 != 0)
            {
                if(fromCost?.until(toCost!!)?.contains(predicate.prices.sale15) == true)
                {
                    return true;
                }
            }
            if(predicate.prices.weekend != null && predicate.prices.weekend != 0)
            {
                if(fromCost?.until(toCost!!)?.contains(predicate.prices.weekend) == true)
                {
                    return true;
                }
            }
            if(predicate.prices.ind != null && predicate.prices.ind != 0)
            {
                if(fromCost?.until(toCost!!)?.contains(predicate.prices.ind) == true)
                {
                    return true;
                }
            }
            return false
        }
    }
    public fun filter(courses: List<CoursePreview>): List<CoursePreview> {
        if(fromCost == null)
        {
            fromCost = 0
        }
        if(toCost == null)
        {
            toCost = 1000000
        }
        return courses.filter {it ->
            filterBase(predicate = it)
        }
    }
}