<?php
/**
 * Created by PhpStorm.
 * User: udantha
 * Date: 6/27/15
 * Time: 1:19 PM
 */

namespace Api\Operation;

use Api\Operation\Request\QueryCriteria;

/**
 * Interface to define crud related methods
 *
 * Interface CrudInterface
 * @package Api\Operation
 */
interface CrudInterface {

    /**
     * Gets a resource by ID
     *
     * @param int $id
     *
     * @return \Httpful\associative|string
     * @throws \Httpful\Exception\ConnectionErrorException
     */
    public function getOne($id);

    /**
     * Gets a list of resources matching the criteria
     *
     * @param QueryCriteria $criteria
     *
     * @return \Httpful\associative|string
     * @throws \Httpful\Exception\ConnectionErrorException
     */
    public function getList(QueryCriteria $criteria);

    /**
     * Creates a resource with data
     *
     * @param string $data
     *
     * @return \Httpful\associative|string
     * @throws \Httpful\Exception\ConnectionErrorException
     */
    public function createOne($data, $headers = array());

    /**
     * Updates a resource
     *
     * @param int    $id
     * @param string $data
     *
     * @return \Httpful\associative|string
     * @throws \Httpful\Exception\ConnectionErrorException
     */
    public function updateOne($id, $data);

    /**
     * Deletes a resource by ID
     *
     * @param int $id
     *
     * @return \Httpful\associative|string
     * @throws \Httpful\Exception\ConnectionErrorException
     */
    public function deleteOne($id);

}