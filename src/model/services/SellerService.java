package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Seller;

public class SellerService {

	//CHAMADA DO DEPARTMENT DAO, PARA RECUPERAR OS VENDEDORES
	private SellerDao dao = DaoFactory.createSellerDao();
	
	//ENCONTRAR TODOS VENDEDORES;
	public List<Seller> findAll(){
			
		return dao.findAll();
	}
	
	//INSERIR OU ATUALIZAR UM OBJETO NO BANCO
	public void saveOrUpdate(Seller obj) {
		
		if(obj.getId() == null) {
			dao.insert(obj);
		}
		else {
			dao.update(obj);
		}
	}
	
	public void remove(Seller obj) {
		dao.deleteById(obj.getId());
	}
	
}
