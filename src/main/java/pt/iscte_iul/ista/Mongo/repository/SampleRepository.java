package pt.iscte_iul.ista.Mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import pt.iscte_iul.ista.Mongo.model.Sample;

public interface SampleRepository extends MongoRepository<Sample, String> {

}
