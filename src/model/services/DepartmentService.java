package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentService {

	//CHAMADA DO DEPARTMENT DAO, PARA RECUPERAR OS DEPARTAMENTOS
	private DepartmentDao dao = DaoFactory.createDepartmentDao();
	
	//ENCONTRAR TODOS DEPARTAMENTOS;
	public List<Department> findAll(){
			
		return dao.findAll();
	}
	
	//INSERIR OU ATUALIZAR UM OBJETO NO BANCO
	public void saveOrUpdate(Department obj) {
		
		if(obj.getId() == null) {
			dao.insert(obj);
		}
		else {
			dao.update(obj);
		}
	}
	
	public void remove(Department obj) {
		dao.deleteById(obj.getId());
	}
	
}
