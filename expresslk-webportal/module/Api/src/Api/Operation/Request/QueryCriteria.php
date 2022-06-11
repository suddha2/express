<?php
/**
 * Created by PhpStorm.
 * User: udantha
 * Date: 6/27/15
 * Time: 1:32 PM
 */

namespace Api\Operation\Request;


class QueryCriteria {

	private $params = array();

    /**
     * Clear existing parameters and Set all the params at once
     * @param $params
     * @return $this
     */
	public function setParams($params)
	{
		$this->params = $params;
        return $this;
	}

    /**
     * Append one parameter to the total paramenters
     * @param $key
     * @param $value
     * @return $this
     */
    public function addParam($key, $value)
    {
        $this->params[$key] = $value;
        return $this;
    }

    /**
     * Load all the results from backend. Use with caution!! This will load all the results in db
     * @return $this
     */
    public function setLoadAll()
    {
        $this->params['pageRows'] = -1;
        return $this;
    }

    /**
     * @param $fieldName string Service field name to sort
     * @return $this
     */
    public function setSortField($fieldName)
    {
        $this->params['sortField'] = $fieldName;
        return $this;
    }

	public function createQuery()
	{
		$query = http_build_query($this->params);
		return preg_replace('/%5B(?:[0-9]|[1-9][0-9]+)%5D=/', '=', $query); // id[0]=2&id[1]=6 => id=2&id=6
	}
}