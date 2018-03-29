package kingdom.bnlive.in.trainermonitorlive;

/**
 * Created by Sk Faisal on 3/26/2018.
 */

public class MergeSheduleUniversity {
    private BatchStatusModel statusModel;
    private TrainerDetailsModel trainerDetailsModel;
   private UniversityDetailsModel university;

    public MergeSheduleUniversity() {
    }

    public MergeSheduleUniversity(BatchStatusModel statusModel, TrainerDetailsModel trainerDetailsModel, UniversityDetailsModel university) {
        this.statusModel = statusModel;
        this.trainerDetailsModel = trainerDetailsModel;
        this.university = university;
    }

    public BatchStatusModel getStatusModel() {
        return statusModel;
    }

    public void setStatusModel(BatchStatusModel statusModel) {
        this.statusModel = statusModel;
    }

    public TrainerDetailsModel getTrainerDetailsModel() {
        return trainerDetailsModel;
    }

    public void setTrainerDetailsModel(TrainerDetailsModel trainerDetailsModel) {
        this.trainerDetailsModel = trainerDetailsModel;
    }

    public UniversityDetailsModel getUniversity() {
        return university;
    }

    public void setUniversity(UniversityDetailsModel university) {
        this.university = university;
    }

    @Override
    public String toString() {
        return "MergeSheduleUniversity{" +
                "statusModel=" + statusModel +
                ", trainerDetailsModel=" + trainerDetailsModel +
                ", university=" + university +
                '}';
    }
}
