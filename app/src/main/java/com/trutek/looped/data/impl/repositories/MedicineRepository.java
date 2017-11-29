package com.trutek.looped.data.impl.repositories;

import android.content.Context;

import com.trutek.looped.data.contracts.models.MedicineModel;
import com.trutek.looped.data.impl.entities.Medicine;
import com.trutek.looped.msas.common.contracts.IModelMapper;
import com.trutek.looped.msas.common.contracts.PageQuery;
import com.trutek.looped.msas.common.repositories.base.BaseRepository;

import java.util.Observer;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by Rinki on 12/3/2016.
 */
public class MedicineRepository extends BaseRepository<Medicine, MedicineModel> {
    private IModelMapper<Medicine, MedicineModel> mapper;
    private AbstractDao<Medicine, Long> dao;

    public MedicineRepository(Context context, IModelMapper<Medicine, MedicineModel> mapper, AbstractDao<Medicine, Long> dao) {
        super(context,mapper, dao, MedicineRepository.class.getSimpleName());
        this.mapper = mapper;
        this.dao = dao;
    }

    @Override
    protected QueryBuilder<Medicine> query(Long id) {
        return null;
    }

    @Override
    protected QueryBuilder<Medicine> query(PageQuery query) {
        return null;
    }

    @Override
    protected void map(MedicineModel model) {

    }

    @Override
    protected void map(Medicine medicine, MedicineModel model) {

    }

    @Override
    protected Medicine newEntity() {
        return null;
    }

    @Override
    public void addObservers(Observer observer) {

    }

    @Override
    public void deleteObservers(Observer observer) {

    }
}
